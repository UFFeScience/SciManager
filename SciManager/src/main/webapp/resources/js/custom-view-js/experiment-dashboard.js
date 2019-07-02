
var experimentDashboardModule = function($) {
    var cumulativeChart, dailyChart;

    return {

        renderExperimentDetails : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-dashboard-tab-url').val(),
                errorMessage: 'Erro ao buscar dashboard do experimento',
                container: '.tab-container .tab-content',
                noFiltersToUrl: true,
                afterAction: function() {
                    experimentDashboardModule.renderExperimentDetailsData();
                    experimentDashboardModule.renderDashboardWorkflowsData();
                    sciManagerCommons.initializeChosen($('.chosen-component-input select'));
                    sciManagerCommons.bindFormValidation($('#create-workflow-form'));
                    sciManagerCommons.loaded('.tab-container .loading-data-async.loading-tab');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.tab-container .loading-data-async.loading-tab');
                }
            };

            filterSearchCommons.loadData(requestData);
        },

        renderExperimentDetailsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.experiment-details-container #search-experiment-details-url').val(),
                errorMessage: 'Erro ao buscar detalhes do experimento',
                container: '.experiment-details-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.experiment-details-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.experiment-details-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.experiment-details-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardWorkflowsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.workflows-container #search-workflows-url').val(),
                errorMessage: 'Erro ao buscar workflows do experimento',
                container: '.workflows-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.workflows-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.workflows-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.workflows-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        }
    }

}(jQuery);
