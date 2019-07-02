$(function() {

	userDetailsModule.init();

});

var userDetailsModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-user-form'));
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));

			sciManagerCommons.triggerSuccessAlert($('.hasSuccessMessage'), $('.hasSuccessMessage').text());
            sciManagerCommons.triggerErrorAlert($('.hasErrorMessage'), $('.hasErrorMessage').text());
			
			userDetailsModule.bindUIEvents();
			userDetailsModule.renderUserGroupsData();
		},

		renderUserGroupsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.user-group-container',
				errorMessage: 'Erro ao buscar dados de grupos do usuário',
				paginationSelector: '.user-group-container .page-link:not(.disabled)',
				tabView: true,
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
			// trigger open upload image modal
		    $('body').off('click', '.upload-profile-image-modal')
					 .on('click', '.upload-profile-image-modal', function() {
		    	$('#image-profile').attr('src', $('.avatar-view img').attr('src'));
		    	$('#avatar-modal').modal('show');
		    });

			// open change password accordion
			$('body').off('click', '#change-password')
					 .on('click', '#change-password', function() {
				userDetailsModule.handlePasswordAccordion();
			});

			// clear password accordion
			$('body').off('hidden.bs.modal', '.bs-edit-user-modal-lg')
					 .on('hidden.bs.modal', '.bs-edit-user-modal-lg', function() {
				userDetailsModule.handleClearPasswordAccordion();
			});

			// close upload image modal
			$('body').off('hidden.bs.modal', '#avatar-modal')
					 .on('hidden.bs.modal', '#avatar-modal', function(e) {

				asyncDataCommons.getProfileImages();
			});

			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                userDetailsModule.renderUsersData();
            });
		},

		handleClearPasswordAccordion : function() {
			if ($('#password').prop('required') || $('#repeated-password').prop('required')) {

				$('#password').prop('required', false);
				$('#repeated-password').prop('required', false);

				$('#change-password').trigger('click');
				$('#change-password .collapse-link').trigger('click');
			}
		},

		handlePasswordAccordion : function() {
			if (!$('#password').prop('required')) {
				$('#password').prop('required', true);
			}
			else {
				$('#password').val('');
				$('#password').prop('required', false);
			}

			if (!$('#repeated-password').prop('required')) {
				$('#repeated-password').prop('required', true);
			}
			else {
				$('#repeated-password').val('');
				$('#repeated-password').prop('required', false);
			}
		},

		handleFillEditUserData : function(targetElement) {
			var username = $('#user-username').text();
			var email = $('#user-email').text();
			var institution = $('#user-institution').text();

			$('#username').val(username);
			$('#email').val(email);
			$('#institution').val(institution);
		},

		handleEditUserSubmit : function() {
			if (!$('#username').val() || !$('#email').val() || ($('#password').prop('required') && !$('#password').val()) ||
			   ($('#repeated-password').prop('required') && !$('#repeated-password').val())) {

				userDetailsModule.handleInvalidEditUserForm();
			}
			else {
				var data = {
					userId: $('#user-id').val(),
					username: $('#username').val(),
					email: $('#email').val(),
					password: $('#password').val(),
				    institution: $('#institution').val(),
					repeatedPassword: $('#repeated-password').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user/edit-user', data)
			        .done(function(data) {
			        	if (sciManagerCommons.isSuccessType(data.type)) {
			        		$('#user-username').text(username);
			        		$('#user-email').text(email);
			        		$('#user-institution').text(institution);
			        		$('.current-user-username').text(username);
				        	$('.bs-edit-user-modal-lg').modal('hide');
				            sciManagerCommons.buildSuccessMessage(data.text);
			        	}
			        	else {
			        		sciManagerCommons.buildErrorMessage(data.text);
			        	}
			        })
			    	.fail(function(data) {
			    		sciManagerCommons.buildErrorMessage('Erro ao editar usuário.');
			    	});
			}
		},

		handleInvalidEditUserForm : function() {
			$('#username').trigger('blur');
			$('#email').trigger('blur');

			if (($('#password').prop('required') && !$('#password').val()) || ($('#repeated-password').prop('required') && !$('#repeated-password').val())) {
				$('#password').trigger('blur');
				$('#repeated-password').trigger('blur');
			}

			sciManagerCommons.unlockActionButton($('#edit-user-submit-button'));
		}
	}

}(jQuery);
