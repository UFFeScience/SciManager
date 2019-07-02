$(function() {

	workflowUserExecutionModule.init();

});

var workflowUserExecutionModule = function($) {

	return {

		init : function() {
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
			workflowUserExecutionModule.renderWorkflowExecutionData();
			workflowUserExecutionModule.bindUIEvents();
		},

		renderWorkflowExecutionData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar histórico de execuções',
				paginationSelector: '.page-link:not(.disabled)',
				afterAction: function() {
					sciManagerCommons.initializeDateComponents();
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.bindPagination(requestData);
			filterSearchCommons.loadData(requestData);
		},

		bindUIEvents : function() {
			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                workflowUserExecutionModule.renderWorkflowExecutionData();
            });

			// handle search select of Workflow of search filter of Workflow Executions
			$('body').off('keyup', '.filter-workflow .chosen-search input')
					 .on('keyup', '.filter-workflow .chosen-search input', function() {
				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.filter-workflow .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/workflow/api/all-workflows?search=' + $('.filter-workflow .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.filter-workflow .filter-workflow-select', url, 'workflowName');
					}
				}, 600);
			});
		},
	}

}(jQuery);
