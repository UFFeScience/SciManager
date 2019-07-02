$(function() {

	authModule.init();

});

var authModule = function($) {

	return {

		init : function() {
			authModule.bindUIEvents();
		},

		bindUIEvents : function() {
			// trigger login on press enter
			$(document).keypress(function(e) {
			    if (e.which === 13) {
			        if (sciManagerCommons.isValid($('#email').val()) && sciManagerCommons.isValid($('#password').val())) {
			        	$('#login-button').trigger('click');
			        }
			    }
			});

			// trigger submit login
			$('body').off('click', '#login-button').on('click', '#login-button', function() {
				$('#login-form').submit();
			});
		}
	}

}(jQuery);
