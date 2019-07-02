$(function() {

	detailedGraphModule.init();

});

var detailedGraphModule = function($) {

	return {

		init : function() {
			detailedGraphModule.bindUIEvents();
		},

		renderGraphData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-detailed-graph-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar grafo detalhado do workflow',
				paramsToKeep: ['tab'],
				afterAction: function() {
					graphModule.updateGraph();
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.loadData(requestData);
		},

		bindUIEvents : function() {
			// trigger save graph
			$('body').off('click', '#save-detailed-graph').on('click', '#save-detailed-graph', function() {
				detailedGraphModule.handleSubmitDetailedGraph();
			});
		},

		handleSubmitDetailedGraph : function() {
			$('.graph-actions').slideToggle('fast');

			$('#graph-container').addClass('static-graph');
			$('#graph-container').removeClass('editable-graph');

			$('#save-detailed-graph').hide();
			$('#cancel-graph').hide();

			var data = {
				workflowId: $('#workflow-id').val(),
				graphCode: $('#graph-editor').val()
			};
			if (!sciManagerCommons.isValid(data.graphCode)) {
				$('#edit-graph').show();
				return;
			}

			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/graph/save-detailed-workflow-graph', data)
		        .done(function(data) {
		        	if (sciManagerCommons.isSuccessType(data.type)) {
		        		if ($('#info-message-trigger') && $('#info-message-trigger').hasClass('hasInfoMessage')) {
		        			PNotify.removeAll();
		        		}
			            sciManagerCommons.buildSuccessMessage(data.text);
		        	}
		        	else {
		        		sciManagerCommons.buildErrorMessage(data.text);
		        	}
		        })
		    	.fail(function(data) {
		    		sciManagerCommons.buildErrorMessage('Erro inesperado ao salvar grafo do workflow.');
		    	});

			$('#edit-graph').show();
		}
	}

}(jQuery);
