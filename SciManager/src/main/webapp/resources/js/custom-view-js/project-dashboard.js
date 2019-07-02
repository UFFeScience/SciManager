$(function() {

    projectDashboardModule.init();

});

var projectDashboardModule = function($) {
    var cumulativeChart, dailyChart;

    return {

        init : function() {
            projectDashboardModule.bindUIEvents();
        },

        bindUIEvents: function() {
            // prevent date input
            $('body').off('keydown', '.metric-period-filter')
                     .on('keydown', '.metric-period-filter', function(e) {
               e.preventDefault();
               return false;
            });

            // unlock generate chart button
            $('body').off('change', '.metric-period-filter')
                     .on('change', '.metric-period-filter', function() {
                projectDashboardModule.unlockGenerateChartButton();
                projectDashboardModule.generateCharts();
            });

            // set group responsible for selected workflow
            $('body').off('change', '.workflow-metric-select')
                     .on('change', '.workflow-metric-select', function() {
                projectDashboardModule.setWorkflowGroup();
            });

            // clear username select filter
            $('body').off('click', '.clear-user-select')
                     .on('click', '.clear-user-select', function() {
                sciManagerCommons.deselectChosenOption('.add-usernames-metric-select');
            });

            // clear phase select filter
            $('body').off('click', '.clear-phase-select')
                     .on('click', '.clear-phase-select', function() {
                sciManagerCommons.deselectChosenOption('.phase-select');
            });

            // clear workflow select filter
            $('body').off('click', '.clear-workflow-select')
                     .on('click', '.clear-workflow-select', function() {
                sciManagerCommons.deselectChosenOption('.workflow-metric-select');
            });

            // generate chart from filter
            $('body').off('click', '#generate-chart-button')
                     .on('click', '#generate-chart-button', function() {
                projectDashboardModule.generateCharts();
            });

            // expand chart metrics
            $('body').off('click', '.expand-metric-chart')
                     .on('click', '.expand-metric-chart', function() {
                projectDashboardModule.expandMetricContainer();
            });
        },

        renderProjectDetails : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-dashboard-tab-url').val(),
                errorMessage: 'Erro ao buscar dashboard do projeto',
                container: '.tab-container .tab-content',
                noFiltersToUrl: true,
                afterAction: function() {
                    projectDashboardModule.renderProjectDetailsData();
                    projectDashboardModule.renderDashboardPhasesData();
                    projectDashboardModule.renderDashboardScientificExperimentsData();

                    if ($('#is-editable').val() === 'true') {
                        sciManagerCommons.initializeDateOptionsComponents('.metric-period-filter', true);
                        projectDashboardModule.renderDashboardTasksData();
                        projectDashboardModule.generateCharts();
                        sciManagerCommons.initializeChosen($('.chosen-component-input select'));
                    }

                    sciManagerCommons.bindFormValidation($('#create-phase-form'));
                    sciManagerCommons.bindFormValidation($('#create-scientific-experiment-form'));
                    sciManagerCommons.bindFormValidation($('#create-task-form'));

                    sciManagerCommons.loaded('.tab-container .loading-data-async.loading-tab');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.tab-container .loading-data-async.loading-tab');
                }
            };

            filterSearchCommons.loadData(requestData);
        },

        renderProjectDetailsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.project-details-container #search-project-details-url').val(),
                errorMessage: 'Erro ao buscar detalhes do projeto',
                container: '.project-details-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.project-details-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.project-details-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.project-details-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardPhasesData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.phases-container #search-phases-url').val(),
                errorMessage: 'Erro ao buscar fases do projeto',
                container: '.phases-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.phases-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.phases-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.phases-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardScientificExperimentsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.experiments-container #search-experiments-url').val(),
                errorMessage: 'Erro ao buscar experimentos do projeto',
                container: '.experiments-container',
                afterAction: function() {
                    sciManagerCommons.initializeChosen($('.chosen-component-input select'));
                    sciManagerCommons.loaded('.experiments-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.experiments-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.experiments-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderDashboardTasksData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('.tasks-container #search-tasks-url').val(),
                errorMessage: 'Erro ao buscar tarefas do projeto',
                container: '.tasks-container',
                afterAction: function() {
                    sciManagerCommons.initializeDateComponents();
                    tasksCommons.initializeDataComponents();
                    sciManagerCommons.loaded('.tasks-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.tasks-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.tasks-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        expandMetricContainer : function() {
            $('.project-metric-container').toggleClass('full-width', 'fast');
            $('.project-metric-container .expand-metric-chart i').toggleClass('fa fa-compress');
            $('.project-metric-container .expand-metric-chart i').toggleClass('fa fa-expand');
            projectDashboardModule.redrawCharts();
        },

        setWorkflowGroup : function() {
            sciManagerCommons.clearChosenSelects('.add-usernames-metric-select');

            var workflowId =  $('.workflow-metric-select option:selected').attr('data-workflow-id');

            $('#workflow-users-metric-select-' + workflowId + ' option').each(function() {
                $('.add-usernames-metric-select')
                    .append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
            });

            $('.add-usernames-metric-select').trigger('chosen:updated');
        },

        unlockGenerateChartButton : function() {
            var periodRange = $('.metric-period-filter').val();

            if (periodRange && periodRange != null && periodRange != '') {
                $('#generate-chart-button').prop('disabled', false);
            }
        },

        generateCharts : function() {
            var scientificProjectId = $('#scientific-project-id').val();
            var phaseId = $('.phase-select').val();
            var workflowId = $('.workflow-metric-select').val();
            var userId = $('.add-usernames-metric-select').val();
            var periodRange = $('.metric-period-filter').val();

            if (!scientificProjectId || scientificProjectId == null || scientificProjectId == '' ||
                periodRange == '' || periodRange == null || !periodRange) {
                sciManagerCommons.buildWarningMessage('Campo de período de data obrigatório.');
            }

            var periodDates = periodRange.split('-');
            if (periodDates.length < 2 || periodDates > 2) {
                sciManagerCommons.buildWarningMessage('Período de data inválido.');
            }

            var initialDate = periodDates[0].trim();
            var finalDate = periodDates[1].trim();
            $('#metricContainer .loading-data-async').removeClass('hide');

            var urlParams = scientificProjectId + '?phaseId=' + phaseId + '&workflowId=' + workflowId + '&initialDate=' + initialDate +
                            '&finalDate=' + finalDate + '&userId=' + userId;

            $.get(sciManagerCommons.getUrlContext(window.location) + '/task-board/api/metric/' + urlParams)
                .done(function(data) {
                    if (data) {
                        projectDashboardModule.buildCumulativeChart('#metric-cumulative', data.tasksCumulativeChart, 'area-spline');
                        projectDashboardModule.buildDailyChart('#metric-daily', data.tasksDailyUpdateChart, 'area-spline', 'Gráfico de atualizações diária');

	                    var titlePosition = projectDashboardModule.getTitlePosition('#metric-cumulative', '#metric-daily');
                        projectDashboardModule.setChartTitle('#metric-cumulative', 'Gráfico de fluxo acumulativo', titlePosition);
                        projectDashboardModule.setChartTitle('#metric-daily', 'Gráfico de atualizações diária', titlePosition);

                        $('svg').css({'width': '100%'});
                        $('#metricContainer .loading-data-async').addClass('hide');
                    }
                })
                .fail(function(data) {
                    sciManagerCommons.buildErrorMessage('Erro ao gerar gráficos com os filtros buscados.');
                    $('#metricContainer .loading-data-async').addClass('hide');
                });
        },

        // build cumulative chart
        buildCumulativeChart : function(containerId, data, chartType) {
            var xAxisLabel = ['Dates'];
            var xAxis = xAxisLabel.concat(data.chartDates);

            var todoDaysLabel = ['A fazer'];
            var todoDays = todoDaysLabel.concat(data.tasksTodoByDay);

            var doingDaysLabel = ['Fazendo'];
            var doingDays = doingDaysLabel.concat(data.taskDoingsByDay);

            var doneDaysLabel = ['Feitas'];
            var doneDays = doneDaysLabel.concat(data.tasksDoneByDay);

            cumulativeChart = c3.generate({
                bindto: containerId,
                data: {
                    x: 'Dates',
                    xFormat: '%d/%m/%Y',
                    columns: [
                        xAxis,
                        todoDays,
                        doingDays,
                        doneDays
                    ],
                    colors: {
                        'A fazer': '#1c84c6',
                        'Fazendo': '#5bc0de',
                        'Feitas': '#1ab394'
                    },
                    type: chartType
                },
                axis: {
                    x: {
                        type: 'timeseries',
                        tick: {
                            format: '%d/%m/%Y'
                        }
                    }
                }
            });
        },

        // build daily activity chart
        buildDailyChart : function(containerId, data, chartType) {
            var xAxisLabel = ['Dates'];
            var xAxis = xAxisLabel.concat(data.chartDates);

            var todoDaysLabel = ['A fazer'];
            var todoDays = todoDaysLabel.concat(data.tasksTodoByDay);

            var doingDaysLabel = ['Fazendo'];
            var doingDays = doingDaysLabel.concat(data.taskDoingsByDay);

            dailyChart = c3.generate({
                bindto: containerId,
                data: {
                    x: 'Dates',
                    xFormat: '%d/%m/%Y',
                    columns: [
                        xAxis,
                        todoDays,
                        doingDays
                    ],
                    colors: {
                        'A fazer': '#1c84c6',
                        'Fazendo': '#5bc0de'
                    },
                    type: chartType
                },
                axis: {
                    x: {
                        type: 'timeseries',
                        tick: {
                            format: '%d/%m/%Y'
                        }
                    }
                }
            });
        },

        getTitlePosition : function(cumulativeContainer, dailyContainer) {
            var cumulativePosition = d3.select(cumulativeContainer + ' svg').node().getBoundingClientRect().width / 2;

            if (cumulativePosition === 0) {
                return d3.select(dailyContainer + ' svg').node().getBoundingClientRect().width / 2;
            }

            return cumulativePosition;
        },

        setChartTitle : function(containerId, chartTitle, titlePosition) {
            d3.select(containerId + '-title').remove()
            d3.select(containerId + ' svg').append('text')
                                           .attr('id', containerId.substring(1, containerId.length) + '-title')
                                           .attr('x', titlePosition)
                                           .attr('y', 16)
                                           .attr('text-anchor', 'middle')
                                           .style('font-size', '1.4em')
                                           .text(chartTitle);
        },

        redrawCharts : function() {
            if (dailyChart && cumulativeChart) {
                var resizeChartInterval = setInterval(function() {
                    dailyChart.resize();
                    cumulativeChart.resize();

                    var titlePosition = projectDashboardModule.getTitlePosition('#metric-cumulative', '#metric-daily');
                    projectDashboardModule.setChartTitle('#metric-cumulative', 'Gráfico de fluxo acumulativo', titlePosition);
                    projectDashboardModule.setChartTitle('#metric-daily', 'Gráfico de atualizações diária', titlePosition);
                }, 20);

                setTimeout(function() {
                    clearInterval(resizeChartInterval);
                }, 400);
            }
        }
    }

}(jQuery);
