$(function() {

	asyncDataCommons.init();

});

var asyncDataCommons = function($) {

	return {

		init : function() {
			asyncDataCommons.getProfileImages();
		},

		getProfileImages : function() {
			var loadedUserProfiles = [];

			$('.img-async-load').each(function(i, image) {
				var userIdAttr = $(this).data('user-id');
				var userId = userIdAttr.replace('img-profile-', '');

				if (loadedUserProfiles.indexOf(userId) < 0) {
					$.get(sciManagerCommons.getUrlContext(window.location) + '/user/profile-image/' + userId)
						.done(function (data) {
							if (data) {
								$('img[data-user-id="' + userIdAttr + '"]').each(function(j, imageToLoad) {
									$(imageToLoad).load(function() {
										$(imageToLoad).removeClass('img-loading-small');
										$(imageToLoad).removeClass('img-loading-big');
										$(imageToLoad).removeClass('img-loading-medium');

										if ($(imageToLoad).hasClass('avatar-image')) {
											var avatarView = $('.avatar-view');

											if (avatarView && avatarView !== undefined && avatarView !== null) {
												$('.img-container.avatar-wrapper img').cropper('reset', true).cropper('replace', data.profileImageContent);
												$(avatarView).addClass('upload-profile-image-modal');
											}
										}
									});
									$(imageToLoad).attr('src', data.profileImageContent);
								});
							}
						 })
						 .fail(function () {}
						 );

					 loadedUserProfiles.push(userId);
				}

			})
		}
	}

}(jQuery);
