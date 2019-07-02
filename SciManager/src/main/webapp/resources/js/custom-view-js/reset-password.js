$(function() {

    resetPasswordModule.init();

});

var resetPasswordModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#reset-password-form'));
            sciManagerCommons.bindFormValidation($('#request-password-form'));
			resetPasswordModule.bindUIEvents();
		},

		bindUIEvents : function() {
			// submit edit-user-password form
			$('body').off('click', '#redefine-password-button')
					 .on('click', '#redefine-password-button', function() {
				resetPasswordModule.handleEditPasswordSubmit();
			});

            // request new password
            $('body').off('click', '#request-password-button')
					 .on('click', '#request-password-button', function() {
				resetPasswordModule.handleRequestNewPasswordSubmit();
			});
		},

		handleEditPasswordSubmit : function() {
            if (!validator.checkAll($('#reset-password-form'))) {
				sciManagerCommons.unlockActionButton($('#redefine-password-button'));
			}
			else {
				var requestData = {
					email: $('#confirm-email').val(),
					newPassword: $('#new-password').val(),
					repeatedNewPassword: $('#repeated-password').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user/reset-password/edit-password', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
                            $('.alert-danger').addClass('hide');
                            $('.alert-danger span.message-description').text('');

                            $('.alert-success').removeClass('hide');
                            $('.alert-success span.message-description').text(data.text);
                        }
						else {
                            $('.alert-success').addClass('hide');
                            $('.alert-success span.message-description').text('');

                            $('.alert-danger').removeClass('hide');
                            $('.alert-danger span.message-description').text(data.text);
						}

						sciManagerCommons.unlockActionButton($('#redefine-password-button'));
					})
					.fail(function(data) {
						sciManagerCommons.unlockActionButton($('#redefine-password-button'));
                        $('.alert-success').addClass('hide');
                        $('.alert-success span.message-description').text('');

                        $('.alert-danger').removeClass('hide');
                        $('.alert-danger span.message-description').text('Erro inesperado ao redefinir senha.');
					});
			}
		},

        handleRequestNewPasswordSubmit : function() {
            if (!validator.checkAll($('#request-password-form'))) {
				sciManagerCommons.unlockActionButton($('#request-password-button'));
			}
			else {
				var requestData = {
					email: $('#email').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/user/reset-password/request-password', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
                            $('.alert-danger').addClass('hide');
                            $('.alert-danger span.message-description').text('');

                            $('.alert-success').removeClass('hide');
                            $('.alert-success span.message-description').text(data.text);
                        }
						else {
                            $('.alert-success').addClass('hide');
                            $('.alert-success span.message-description').text('');

                            $('.alert-danger').removeClass('hide');
                            $('.alert-danger span.message-description').text(data.text);
						}

						sciManagerCommons.unlockActionButton($('#request-password-button'));
					})
					.fail(function(data) {
						sciManagerCommons.unlockActionButton($('#request-password-button'));
                        $('.alert-success').addClass('hide');
                        $('.alert-success span.message-description').text('');

                        $('.alert-danger').removeClass('hide');
                        $('.alert-danger span.message-description').text('Erro inesperado ao requisitar nova senha.');
					});
			}
		}
	}

}(jQuery);
