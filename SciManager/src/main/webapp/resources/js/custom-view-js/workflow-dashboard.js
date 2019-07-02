$(function() {

	workflowDashboardModule.init();

});

var workflowDashboardModule = function($) {

    return {

        init : function() {
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
            workflowDashboardModule.bindUIEvents();
		},

        bindUIEvents : function() {
            // trigger edit workflow version
			$('body').off('click', '.edit-version-workflow')
					 .on('click', '.edit-version-workflow', function() {
				workflowDashboardModule.handleFillEditWorkflowVersion(this);
			});

            // special mask and validation for version
			$('body').off('keypress', '#edit-workflow-version')
					 .on('keypress', '#edit-workflow-version', function(event) {
				workflowDashboardModule.handleVersionMaskValidation(event, this);
			});

			// disable paste values for version input
			$('body').off('paste', '#edit-workflow-version')
					 .on('paste', '#edit-workflow-version', function(event) {
				event.preventDefault();
			});

			// handle edit workflow version submit
			$('body').off('click', '#edit-workflow-version-submit-button')
					 .on('click', '#edit-workflow-version-submit-button', function() {
				workflowDashboardModule.handleEditVersionSubmit();
			});
        },

        renderWorkflowDetails : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-dashboard-tab-url').val(),
                errorMessage: 'Erro ao buscar dashboard do workflow',
                container: '.tab-container .tab-content',
                noFiltersToUrl: true,
                afterAction: function() {
                    workflowDashboardModule.renderWorkflowDetailsData();
                    workflowDashboardModule.renderDashboardModelFilesData();
                    workflowDashboardModule.renderDashboardExecutionHistoryData();

                    sciManagerCommons.loaded('.tab-container .loading-data-async.loading-tab');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.tab-container .loading-data-async.loading-tab');
                }
            };

            filterSearchCommons.loadData(requestData);
        },

        renderWorkflowDetailsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.workflow-details-container #search-workflow-details-url').val(),
                errorMessage: 'Erro ao buscar detalhes do workflow',
                container: '.workflow-details-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.workflow-details-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.workflow-details-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.workflow-details-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardModelFilesData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.model-files-container #search-model-files-url').val(),
                errorMessage: 'Erro ao buscar arquivos modelo do projeto',
                container: '.model-files-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.model-files-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.model-files-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.model-files-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardExecutionHistoryData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.workflow-execution-history-container #search-execution-history-url').val(),
                errorMessage: 'Erro ao buscar histórico de execução do workflow',
                container: '.workflow-execution-history-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.workflow-execution-history-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.workflow-execution-history-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.workflow-execution-history-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        handleFillEditWorkflowVersion : function(targetElement) {
			var workflowId = $(targetElement).attr('id').replace('workflow-version-', '');

			$('#edit-version-workflow-id').val(workflowId);
			$('#edit-workflow-version').val($('#workflow-current-version-number').text());

			$('.bs-edit-workflow-version-modal-lg').modal();
		},

		handleVersionMaskValidation : function(event, target) {
			var textValue = $(target).val();
			var numericValue = +textValue;

			if (numericValue >= 1000) {
				event.preventDefault();
				return false;
			}

			var splittedValue = textValue.split('.')

			if (splittedValue.length > 1 && splittedValue[1].length >= 3) {
				event.preventDefault();
				return false;
			}

			if ((event.which == 46 && (!$(target).val() || $(target).val() == null)) ||
				(event.which != 46 || $(target).val().indexOf('.') != -1) && (event.which < 48 || event.which > 57)) {

				event.preventDefault();
				return false;
			}
		},

        handleEditVersionSubmit : function() {
			if (!sciManagerCommons.isValid($('#edit-workflow-version').val())) {
				$('#edit-workflow-version').trigger('blur');
				sciManagerCommons.unlockActionButton($('#edit-workflow-version-submit-button'));
			}
			else {
				var requestData = {
					workflowId: $('#edit-version-workflow-id').val(),
					version: $('#edit-workflow-version').val()
				};
				if (requestData.version.split('.').length == 1) {
					requestData.version += '.0';
				}

				$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/edit-workflow-version', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('#workflow-current-version-number').text(requestData.version);
							$('.bs-edit-workflow-version-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#edit-workflow-version-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar versão do workflow.');
						sciManagerCommons.unlockActionButton($('#edit-workflow-version-submit-button'));
					});
			}
		}
    }

}(jQuery);
