$(function() {

	notificationMessageModule.init();

});

var notificationMessageModule = function($) {

	return {

		init : function() {
            notificationMessageModule.bindUIEvents();
			notificationMessageModule.renderNotificationsData();
		},

        bindUIEvents : function() {
			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                notificationMessageModule.renderNotificationsData();
            });
        },

        renderNotificationsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar notificações do usuário',
				paginationSelector: '.page-link:not(.disabled)',
				afterAction: function() {
					sciManagerCommons.loaded();
					notificationsCommons.getNotificationsCount();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.bindPagination(requestData);
			filterSearchCommons.loadData(requestData);
        }
	}

}(jQuery);
