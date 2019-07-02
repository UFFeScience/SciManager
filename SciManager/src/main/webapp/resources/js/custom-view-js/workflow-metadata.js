$(function() {

	workflowMetadataModule.init();

});

var workflowMetadataModule = function($) {

	return {

		init : function() {
			workflowMetadataModule.renderExecutionMetadata();
			workflowMetadataModule.getTotalMetadata();
		},

		renderExecutionMetadata : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar metadados de execução de workflow',
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

		// verify if the number of metadata has increased
		getTotalMetadata : function() {
			var workflowId = $('#workflow-id').val();
			var executionId = $('#execution-id').val();

			 $.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/' + workflowId + '/count-metadata/' + executionId)
			 	.done(function(data) {
					 var currentTotalMetadata = +$('#current-total-metadata').val();

					 if (+data > currentTotalMetadata) {
						 $('#current-total-metadata').val(data);
						 $('.refresh-data ').removeClass('hide');
					 }
					 else {
						 setTimeout(workflowMetadataModule.getTotalMetadata, 30000);
					 }
				 })
				 .fail(function() {});
		}
	}

}(jQuery);
