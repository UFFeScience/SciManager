$(function() {

    workflowDetailsModule.init();

});

var workflowDetailsModule = function($) {
    var currentTab;

    return {

        init : function() {
            workflowDetailsModule.renderTab();
            workflowDocumentationModule.init();
            modelFileModule.init();
            macroGraphModule.init();
            detailedGraphModule.init();
            workflowExecutionModule.init();
            workflowDetailsModule.bindUIEvents();
        },

        renderTab : function() {
            var selectedTab = sciManagerCommons.getRequestParamValue(window.location, 'tab');
            currentTab = selectedTab;
            $('.tabs-menu li').removeClass('active');

            if (selectedTab === 'dashboard' || selectedTab === undefined) {
                $('.tabs-menu li.workflow-dashboard').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                currentTab = 'dashboard';
                workflowDashboardModule.renderWorkflowDetails();
            }
            else if (selectedTab === 'model-files') {
                $('.tabs-menu li.workflow-model-files').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                modelFileModule.renderModelFilesData();
            }
            else if (selectedTab === 'detailed-graph') {
                $('.tabs-menu li.workflow-detailed-graph').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                detailedGraphModule.renderGraphData();
            }
            else if (selectedTab === 'macro-graph') {
                $('.tabs-menu li.workflow-macro-graph').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                macroGraphModule.renderGraphData();
            }
            else if (selectedTab === 'execution-history') {
                $('.tabs-menu li.workflow-execution-history').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                workflowExecutionModule.renderWorkflowExecutionData();
            }
            else if (selectedTab === 'documentation') {
                $('.tabs-menu li.workflow-documentation').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                workflowDocumentationModule.renderDocumentationData();
            }
        },

        bindUIEvents : function() {
            // go to model files tab
            $('body').off('click', '.workflow-model-files')
                     .on('click', '.workflow-model-files', function() {
                workflowDetailsModule.renderModelFilesTab();
            });

            // go to dashboard tab
            $('body').off('click', '.workflow-dashboard')
                     .on('click', '.workflow-dashboard', function() {
                workflowDetailsModule.renderDashboardTab();
            });

            // go to documentation tab
            $('body').off('click', '.workflow-documentation')
                     .on('click', '.workflow-documentation', function() {
                workflowDetailsModule.renderDocumentationTab();
            });

            // go to execution history tab
            $('body').off('click', '.workflow-execution-history')
                     .on('click', '.workflow-execution-history', function() {
                workflowDetailsModule.renderExecutionHistoryTab();
            });

            // go to detailed graph tab
            $('body').off('click', '.workflow-detailed-graph')
                     .on('click', '.workflow-detailed-graph', function() {
                workflowDetailsModule.renderDetailedGraphTab();
            });

            // go to macro graph tab
            $('body').off('click', '.workflow-macro-graph')
                     .on('click', '.workflow-macro-graph', function() {
                workflowDetailsModule.renderMacroGraphTab();
            });

            // on close modal
			$('body').off('hidden.bs.modal', '.bs-edit-workflow-modal-lg')
					 .on('hidden.bs.modal', '.bs-edit-workflow-modal-lg', function() {
                workflowDetailsModule.verifyWorkflowPermission();
			});

            // listener to refresh model files data
            document.removeEventListener('modelFileRefresh', workflowDetailsModule.refreshModelFilesData.bind(this));
            document.addEventListener('modelFileRefresh', workflowDetailsModule.refreshModelFilesData.bind(this));

            // handle browser back button
            $(window).off('popstate').on('popstate', function() {
                workflowDetailsModule.renderTab();
            });
        },

        renderModelFilesTab : function() {
            if (currentTab !== 'model-files') {
                currentTab = 'model-files';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-model-files').addClass('active');
                modelFileModule.renderModelFilesData();
            }
        },

        renderDashboardTab : function() {
            if (currentTab !== 'dashboard') {
                currentTab = 'dashboard';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-dashboard').addClass('active');
                sciManagerCommons.clearDateComponents();
                workflowDashboardModule.renderWorkflowDetails();
            }
        },

        renderDocumentationTab : function() {
            if (currentTab !== 'documentation') {
                currentTab = 'documentation';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-documentation').addClass('active');
                workflowDocumentationModule.renderDocumentationData();
            }
        },

        renderExecutionHistoryTab : function() {
            if (currentTab !== 'execution-history') {
                currentTab = 'execution-history';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-execution-history').addClass('active');
                workflowExecutionModule.renderWorkflowExecutionData();
            }
        },

        renderDetailedGraphTab : function() {
            if (currentTab !== 'detailed-graph') {
                currentTab = 'detailed-graph';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-detailed-graph').addClass('active');
                sciManagerCommons.clearDateComponents();
                detailedGraphModule.renderGraphData();
            }
        },

        renderMacroGraphTab : function() {
            if (currentTab !== 'macro-graph') {
                currentTab = 'macro-graph';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.workflow-macro-graph').addClass('active');
                sciManagerCommons.clearDateComponents();
                macroGraphModule.renderGraphData();
            }
        },

        refreshModelFilesData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                if (currentTab === 'model-files') {
                    modelFileModule.renderModelFilesData();
                } else if (currentTab === 'dashboard') {
                    workflowDashboardModule.renderDashboardModelFilesData();
                }
            }
        },

        verifyWorkflowPermission : function() {
            var workflowId = $('.edit-workflow').attr('id').replace('edit-workflow-', '');

            $.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/api/permission/' + workflowId)
                .done(function (data) {
                    if (data.statusCode === 403) {
                        var scientificExperimentId = $('#experiment-id-' + workflowId).val()
                        var message = "Você não tem mais acesso a esse workflow.<br />" +
                            "<b><a href=\"" + sciManagerCommons.getUrlContext(window.location) + "/scientific-experiment/" +
                            scientificExperimentId + "/experiment-details?tab=workflows\">Ir para listagem de workflows.</a></b>";

                        sciManagerCommons.loading('.tab-container .loading-data-async.loading-tab');
                        sciManagerCommons.buildWarningMessage(message, false);
                    }
                });
        }
    }

}(jQuery);
