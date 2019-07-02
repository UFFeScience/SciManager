$(function() {

	scientificProjectListModule.init();

});

var scientificProjectListModule = function($) {

	return {

		init : function() {
			scientificProjectCommons.init();
            scientificProjectListModule.bindUIEvents();
			scientificProjectListModule.renderProjectsData();
		},

        bindUIEvents : function() {
            // listener to refresh projects data
            document.removeEventListener('projectsRefresh', scientificProjectListModule.refreshProjectsData.bind(this));
            document.addEventListener('projectsRefresh', scientificProjectListModule.refreshProjectsData.bind(this));

			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                scientificProjectListModule.renderProjectsData();
            });
        },

        renderProjectsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar projetos científicos',
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

        refreshProjectsData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                scientificProjectListModule.renderProjectsData()
            }
        }
	}

}(jQuery);
