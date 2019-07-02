$(function() {

    experimentDetailsModule.init();

});

var experimentDetailsModule = function($) {
    var currentTab;

    return {

        init : function() {
            experimentDetailsModule.renderTab();
            scientificExperimentDocumentationModule.init();
            scientificExperimentModule.init();
            workflowModule.init();
            experimentDetailsModule.bindUIEvents();
        },

        renderTab : function() {
            var selectedTab = sciManagerCommons.getRequestParamValue(window.location, 'tab');
            currentTab = selectedTab;
            $('.tabs-menu li').removeClass('active');

            if (selectedTab === 'dashboard' || selectedTab === undefined) {
                $('.tabs-menu li.experiment-dashboard').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                currentTab = 'dashboard';
                experimentDashboardModule.renderExperimentDetails();
            }
            else if (selectedTab === 'workflows') {
                $('.tabs-menu li.experiment-workflows').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                workflowModule.renderWorkflowsData();
            }
            else if (selectedTab === 'documentation') {
                $('.tabs-menu li.experiment-documentation').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                scientificExperimentDocumentationModule.renderDocumentationData();
            }
        },

        bindUIEvents : function() {
            // go to workflows tab
            $('body').off('click', '.experiment-workflows')
                     .on('click', '.experiment-workflows', function() {
                experimentDetailsModule.renderWorkflowTab();
            });

            // go to dashboard tab
            $('body').off('click', '.experiment-dashboard')
                     .on('click', '.experiment-dashboard', function() {
                experimentDetailsModule.renderDashboardTab();
            });

            // go to documentation tab
            $('body').off('click', '.experiment-documentation')
                     .on('click', '.experiment-documentation', function() {
                experimentDetailsModule.renderDocumentationTab();
            });

            // listener to refresh workflows data
            document.removeEventListener('workflowRefresh', experimentDetailsModule.refreshWorkflowsData.bind(this));
            document.addEventListener('workflowRefresh', experimentDetailsModule.refreshWorkflowsData.bind(this));

            // handle browser back button
            $(window).off('popstate').on('popstate', function() {
                experimentDetailsModule.renderTab();
            });
        },

        renderWorkflowTab : function() {
            if (currentTab !== 'experiments') {
                currentTab = 'workflows';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.experiment-workflows').addClass('active');
                workflowModule.renderWorkflowsData();
            }
        },

        renderDashboardTab : function() {
            if (currentTab !== 'dashboard') {
                currentTab = 'dashboard';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.experiment-dashboard').addClass('active');
                experimentDashboardModule.renderExperimentDetails();
            }
        },

        renderDocumentationTab : function() {
            if (currentTab !== 'documentation') {
                currentTab = 'documentation';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.experiment-documentation').addClass('active');
                scientificExperimentDocumentationModule.renderDocumentationData();
            }
        },

        refreshWorkflowsData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                if (currentTab === 'workflows') {
                    workflowModule.renderWorkflowsData();
                } else if (currentTab === 'dashboard') {
                    experimentDashboardModule.renderDashboardWorkflowsData();
                    experimentDashboardModule.renderExperimentDetailsData();
                }
            }
        }
    }

}(jQuery);
