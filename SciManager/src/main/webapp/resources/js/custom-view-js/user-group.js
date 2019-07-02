$(function() {

	userGroupModule.init();

});

var userGroupModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-user-group-form'));
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
			userGroupModule.bindUIEvents();
			userGroupModule.renderUserGroupsData();
		},

		renderUserGroupsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar grupos de usuários',
				paginationSelector: '.page-link:not(.disabled)',
				afterAction: function() {
					sciManagerCommons.initializeChosen($('.chosen-component-input select'));
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
			// submit create-usergroup form
			$('body').off('click', '#create-user-group-submit-button')
					 .on('click', '#create-user-group-submit-button', function() {
				userGroupModule.handleCreateUserGroupSubmit(this);
			});

			// trigger edit usergroup name modal
			$('body').off('click', '.edit-user-group')
					 .on('click', '.edit-user-group', function() {
				userGroupModule.handleFillEditUserGroupName(this);
			});

			// handle edit usergroup name submit
			$('body').off('click', '#edit-user-group-name-submit-button')
					 .on('click', '#edit-user-group-name-submit-button', function() {
				userGroupModule.handleEditUserGroupNameSubmit();
			});

			// trigger remove usergroup
		    $('body').off('click', '.remove-user-group-button')
					 .on('click', '.remove-user-group-button', function() {
				userGroupModule.handleFillRemoveUserGroupData(this);
		    });

		    // submit remove usergroup
		    $('body').off('click', '#delete-user-group-button')
					 .on('click', '#delete-user-group-button', function() {
				userGroupModule.handleRemoveUserGroupSubmit();
		    });

			// on close remove usergroup modal
			$('body').off('hidden.bs.modal', '.bs-remove-user-group-modal-lg')
					 .on('hidden.bs.modal', '.bs-remove-user-group-modal-lg', function() {
				$('#dependencies-message').text('');
				$('#delete-user-group-button').prop('disabled', false);
				$('#deleteUserGroupLabel').removeClass('hide');
				$('#canNotDeleteUserGroupLabel').addClass('hide');
			});

			// trigger remove user from group
			$('body').off('click', '.remove-user-from-group')
					 .on('click', '.remove-user-from-group', function() {
				userGroupModule.handleFillRemoveUserFromGroupData(this);
			});

			// submit remove-user form
			$('body').off('click', '#remove-user-submit-button')
					 .on('click', '#remove-user-submit-button', function() {
				userGroupModule.handleRemoveUserFromGroupSubmit();
			});

			// trigger add user to group
			$('body').off('click', '.add-user')
					 .on('click', '.add-user', function() {
				userGroupModule.handleFillAddUserToGroupData(this);
			});

			// submit add-users form
			$('body').off('click', '#add-users-submit-button')
					 .on('click', '#add-users-submit-button', function() {
				userGroupModule.handleAddUserToGroupSubmit();
			});

			// handle search select of users of new UserGroup
			$('body').off('keyup', '.new-usernames .chosen-container input')
					 .on('keyup', '.new-usernames .chosen-container input', function() {
				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.new-usernames .chosen-container input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/user/api/all-users?search=' + $('.new-usernames .chosen-container input').val();
						sciManagerCommons.handleChosenSearch('.new-usernames .new-usernames-select', url, 'email', 'username');
					}
				}, 600);
			});

			// handle search select of users of existing UserGroup
			$('body').off('keyup', '.add-usernames .chosen-container input')
					 .on('keyup', '.add-usernames .chosen-container input', function() {
				sciManagerCommons.typewatch(function() {
					var userGroupId = $('#add-user-user-group-id').val();
					if (sciManagerCommons.isValid($('.add-usernames .chosen-container input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/user/api/all-new-users/' + userGroupId + '?search=' + $('.add-usernames .chosen-container input').val();
						sciManagerCommons.handleChosenSearch('.add-usernames .add-usernames-select', url, 'email', 'username');
					}
				}, 600);
			});

			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                userGroupModule.renderUserGroupsData();
            });
		},

		handleCreateUserGroupSubmit : function() {
			if (!validator.checkAll($('#create-user-group-form'))) {
				sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user-group/create-user-group', $('#create-user-group-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-group-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							userGroupModule.renderUserGroupsData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar grupo de usuários.');
						sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
					});
			}
		},

		handleFillRemoveUserGroupData : function(targetElement) {
			var userGroupId = $(targetElement).attr('id').replace('remove-user-group-', '');
	    	$('#remove-user-group-id').val(userGroupId);

	    	$.get(sciManagerCommons.getUrlContext(window.location) + '/user-group/count-dependencies/' + userGroupId)
				.done(function (data) {
					if (data && data != null && data != undefined && data.workflows > 0) {
						$('#deleteUserGroupLabel').addClass('hide');
						$('#canNotDeleteUserGroupLabel').removeClass('hide');

						var canNotDeleteMessage = data.workflows + ' workflows';

						$('#dependencies-message').text(canNotDeleteMessage);
						$('#delete-user-group-button').prop('disabled', true);
					}
				});
		},

		handleRemoveUserGroupSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/user-group/remove-user-group', {userGroupId: $('#remove-user-group-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-user-group-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						userGroupModule.renderUserGroupsData();
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#delete-user-group-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover grupo de usuários.');
					sciManagerCommons.unlockActionButton($('#delete-user-group-button'));
				});
		},

		handleFillEditUserGroupName : function(targetElement) {
			var userGroupId = $(targetElement).attr('id').replace('edit-user-group-', '');
			$('#edit-user-group-name-id').val(userGroupId);

			var userGroupName = $('#group-name-' + userGroupId).text();
			$('#edit-user-group-name-value').val(userGroupName);
		},

		handleEditUserGroupNameSubmit : function() {
			if (!sciManagerCommons.isValid($('#edit-user-group-name-value').val())) {
				$('#edit-user-group-name-value').trigger('blur');
				sciManagerCommons.unlockActionButton($('#edit-user-group-name-submit-button'));
			}
			else {
				var data = {
					userGroupId: $('#edit-user-group-name-id').val(),
					groupName: $('#edit-user-group-name-value').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user-group/edit-name', data)
			        .done(function(responseData) {
			        	if (sciManagerCommons.isSuccessType(responseData.type)) {
			        		$('#group-name-' + data.userGroupId).text(data.groupName);
				        	$('.bs-edit-name-modal-lg').modal('hide');
				            sciManagerCommons.buildSuccessMessage(responseData.text);
			        	}
			        	else {
			        		sciManagerCommons.buildErrorMessage(responseData.text);
			        	}

						sciManagerCommons.unlockActionButton($('#edit-user-group-name-submit-button'));
			        })
			    	.fail(function(data) {
			    		sciManagerCommons.buildErrorMessage('Erro ao editar nome do grupo.');
						sciManagerCommons.unlockActionButton($('#edit-user-group-name-submit-button'));
			    	});
			}
		},

		handleFillRemoveUserFromGroupData : function(targetElement) {
			var idsString = $(targetElement).attr('id').replace('remove-group-user-', '');
			var idsArray = idsString.split('-');

			var userId = idsArray[0];
			var userGroupId = idsArray[1];

			$('#remove-user-user-id').val(userId);
			$('#remove-user-user-group-id').val(userGroupId);
		},

		handleRemoveUserFromGroupSubmit : function() {
			var data = {
				userGroupId: $('#remove-user-user-group-id').val(),
				userId: $('#remove-user-user-id').val()
			};
			$.post(sciManagerCommons.getUrlContext(window.location) + '/user-group/remove-user', data)
				.done(function(responseData) {
					if (sciManagerCommons.isSuccessType(responseData.type)) {
						var liToRemove = $('#remove-group-user-' + data.userId + '-' + data.userGroupId).parents('li');

						$(liToRemove).fadeOut(300, function() {
							$(liToRemove).remove();
						 });

						sciManagerCommons.buildSuccessMessage(responseData.text);
					}
					else {
						sciManagerCommons.buildErrorMessage(responseData.text);
					}

					sciManagerCommons.unlockActionButton($('#remove-user-submit-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover usuário do grupo.');
					sciManagerCommons.unlockActionButton($('#remove-user-submit-button'));
				});
		},

		handleFillAddUserToGroupData : function(targetElement) {
			var userGroupId = $(targetElement).attr('id').replace('add-user-', '');
			$('#add-user-user-group-id').val(userGroupId);

			$('.add-usernames .add-usernames-select').empty()
			$('.add-usernames .add-usernames-select').trigger('chosen:updated');
		},

		handleAddUserToGroupSubmit : function() {
			if (!sciManagerCommons.isValid($('.add-usernames-select').val())) {
				sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user-group/add-users', $('#add-users-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-add-user-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							userGroupModule.renderUserGroupsData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao adicionar usuário a grupo de usuários.');
						sciManagerCommons.unlockActionButton($('#create-user-group-submit-button'));
					});
			}
		}
	}

}(jQuery);
