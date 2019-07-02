$(function() {

	workflowExecutionModule.init();

});

var workflowExecutionModule = function($) {

	return {

		init : function() {
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
		},

		renderWorkflowExecutionData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-execution-history-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar histórico de execuções',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
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
		}
	}

}(jQuery);
