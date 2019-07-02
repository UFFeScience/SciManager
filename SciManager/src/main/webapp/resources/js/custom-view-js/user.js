$(function() {

	userModule.init();

});

var userModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-user-form'));
			userModule.bindUIEvents();
			userModule.renderUsersData();
		},

		renderUsersData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar usuários',
				paginationSelector: '.page-link:not(.disabled)',
				afterAction: function() {
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.bindPagination(requestData);
			filterSearchCommons.loadData(requestData);
        },

		bindUIEvents: function() {
			// submit create-user form
			$('body').off('click', '#create-user-submit-button')
					 .on('click', '#create-user-submit-button', function() {
				userModule.handleCreateUserSubmit(this);
			});

			// trigger edit user modal
			$('body').off('click', '.edit-user')
					 .on('click', '.edit-user', function() {
				userModule.handleFillEditUserData(this);
			});

			// handle edit user submit
			$('body').off('click', '#edit-user-submit-button')
					 .on('click', '#edit-user-submit-button', function() {
				userModule.handleEditUserSubmit();
			});

			// trigger edit user role modal
			$('body').off('click', '.change-role')
					 .on('click', '.change-role', function() {
				userModule.handleFillEditUserRoleData(this);
			});

			// handle edit user role submit
			$('body').off('click', '#change-user-role-button')
					 .on('click', '#change-user-role-button', function() {
				userModule.handleEditUserRoleSubmit();
			});

			// trigger remove user
		    $('body').off('click', '.remove-user-button')
					 .on('click', '.remove-user-button', function() {
				userModule.handleFillRemoveUserData(this);
		    });

		    // submit remove user
		    $('body').off('click', '#delete-user-button')
					 .on('click', '#delete-user-button', function() {
				userModule.handleRemoveUserSubmit();
		    });

			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                userModule.renderUsersData();
            });
		},

		handleCreateUserSubmit : function() {
			if (!validator.checkAll($('#create-user-form'))) {
				sciManagerCommons.unlockActionButton($('#create-user-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user/create-user', $('#create-user-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-user-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							userModule.renderUsersData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-user-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar usuário.');
						sciManagerCommons.unlockActionButton($('#create-user-submit-button'));
					});
			}
		},

		handleFillRemoveUserData : function(targetElement) {
			var userId = $(targetElement).attr('id').replace('remove-user-', '');
	    	$('#remove-user-id').val(userId);
		},

		handleRemoveUserSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/user/remove-user', {userId: $('#remove-user-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-user-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						userModule.renderUsersData();
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#delete-user-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover usuário.');
					sciManagerCommons.unlockActionButton($('#delete-user-button'));
				});
		},

		handleFillEditUserRoleData : function(targetElement) {
			var userId = $(targetElement).attr('id').replace('role-', '');
			var newRole = $(targetElement).hasClass('role-user') ? 'USER' : 'ADMIN';

			$('#new-role').text($(targetElement).hasClass('role-user') ? '"Usuário"' : '"Administrador"')

			$('#user-id').val(userId);
			$('#user-role').val(newRole);
		},

		handleEditUserRoleSubmit : function() {
			if (!validator.checkAll($('#edit-user-role-form'))) {
				sciManagerCommons.unlockActionButton($('#change-user-role-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user/edit-user-role', $('#edit-user-role-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-change-user-role-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							userModule.renderUsersData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#change-user-role-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao alterar permissionamento de usuário.');
						sciManagerCommons.unlockActionButton($('#change-user-role-button'));
					});
			}
		}
	}

}(jQuery);
