$(function() {

    projectDetailsModule.init();

});

var projectDetailsModule = function($) {
    var currentTab;

    return {

        init : function() {
            projectDetailsModule.renderTab();
            projectDashboardModule.init();
            scientificProjectDocumentationModule.init();
            scientificExperimentModule.init();
            taskBoardModule.init();
            taskListModule.init();
            phaseModule.init();
            projectDetailsModule.bindUIEvents();
        },

        renderTab : function() {
            var selectedTab = sciManagerCommons.getRequestParamValue(window.location, 'tab');
            currentTab = selectedTab;
            $('.tabs-menu li').removeClass('active');

            if (selectedTab === 'dashboard' || selectedTab === undefined) {
                $('.tabs-menu li.project-dashboard').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                currentTab = 'dashboard';
                sciManagerCommons.clearDateComponents();
                projectDashboardModule.renderProjectDetails();
            }
            else if (selectedTab === 'experiments') {
                $('.tabs-menu li.project-experiments').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                scientificExperimentModule.renderExperimentsData();
            }
            else if (selectedTab === 'phases') {
                $('.tabs-menu li.project-phases').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                phaseModule.renderPhasesData();
            }
            else if (selectedTab === 'documentation') {
                $('.tabs-menu li.project-documentation').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                scientificProjectDocumentationModule.renderDocumentationData();
            }
            else if (selectedTab === 'taskBoard') {
                $('.tabs-menu li.project-board').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                taskBoardModule.renderTaskBoardData();
            }
            else if (selectedTab === 'taskList') {
                $('.tabs-menu li.project-tasks').addClass('active');
                sciManagerCommons.updateTabUrl(currentTab);
                taskListModule.renderTaskListData();
            }
        },

        bindUIEvents : function() {
            // go to experiments tab
            $('body').off('click', '.project-experiments')
                     .on('click', '.project-experiments', function() {
                projectDetailsModule.renderExperimentsTab();
            });

            // go to dashboard tab
            $('body').off('click', '.project-dashboard')
                     .on('click', '.project-dashboard', function() {
                projectDetailsModule.renderDashboardTab();
            });

            // go to documentation tab
            $('body').off('click', '.project-documentation')
                     .on('click', '.project-documentation', function() {
                projectDetailsModule.renderDocumentationTab();
            });

            // go to phases tab
            $('body').off('click', '.project-phases')
                     .on('click', '.project-phases', function() {
                projectDetailsModule.renderPhasesTab();
            });

            // go to tasks tab
            $('body').off('click', '.project-tasks')
                     .on('click', '.project-tasks', function() {
                projectDetailsModule.renderTaskListTab();
            });

            // go to task board tab
            $('body').off('click', '.project-board')
                     .on('click', '.project-board', function() {
                projectDetailsModule.renderTaskBoardTab();
            });

            // listener to refresh tasks data
            document.removeEventListener('taskRefresh', projectDetailsModule.refreshTasksData.bind(this));
            document.addEventListener('taskRefresh', projectDetailsModule.refreshTasksData.bind(this));

            // listener to refresh experiments data
            document.removeEventListener('experimentRefresh', projectDetailsModule.refreshExperimentsData.bind(this));
            document.addEventListener('experimentRefresh', projectDetailsModule.refreshExperimentsData.bind(this));

            // listener to refresh phases data
            document.removeEventListener('phaseRefresh', projectDetailsModule.refreshPhasesData.bind(this));
            document.addEventListener('phaseRefresh', projectDetailsModule.refreshPhasesData.bind(this));

            // handle browser back button
            $(window).off('popstate').on('popstate', function() {
                projectDetailsModule.renderTab();
            });
        },

        renderExperimentsTab : function() {
            if (currentTab !== 'experiments') {
                currentTab = 'experiments';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-experiments').addClass('active');
                scientificExperimentModule.renderExperimentsData();
            }
        },

        renderDashboardTab : function() {
            if (currentTab !== 'dashboard') {
                currentTab = 'dashboard';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-dashboard').addClass('active');
                sciManagerCommons.clearDateComponents();
                projectDashboardModule.renderProjectDetails();
            }
        },

        renderDocumentationTab : function() {
            if (currentTab !== 'documentation') {
                currentTab = 'documentation';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-documentation').addClass('active');
                scientificProjectDocumentationModule.renderDocumentationData();
            }
        },

        renderPhasesTab : function() {
            if (currentTab !== 'phases') {
                currentTab = 'phases';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-phases').addClass('active');
                phaseModule.renderPhasesData();
            }
        },

        renderTaskListTab : function() {
            if (currentTab !== 'taskList') {
                currentTab = 'taskList';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-tasks').addClass('active');
                sciManagerCommons.clearDateComponents();
                taskListModule.renderTaskListData();
            }
        },

        renderTaskBoardTab : function() {
            if (currentTab !== 'taskBoard') {
                currentTab = 'taskBoard';
                sciManagerCommons.updateTabUrl(currentTab);
                $('.tabs-menu li').removeClass('active');
                $('.tabs-menu li.project-board').addClass('active');
                sciManagerCommons.clearDateComponents();
                taskBoardModule.renderTaskBoardData();
            }
        },

        refreshTasksData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                if (currentTab === 'taskList') {
                    taskListModule.renderTaskListData();
                } else if (currentTab === 'taskBoard') {
                    taskBoardModule.renderTaskBoardData();
                } else if (currentTab === 'dashboard') {
                    projectDashboardModule.renderDashboardTasksData();
                    projectDashboardModule.renderProjectDetailsData();
                }
            }
        },

        refreshExperimentsData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                if (currentTab === 'experiments') {
                    scientificExperimentModule.renderExperimentsData();
                } else if (currentTab === 'dashboard') {
                    projectDashboardModule.renderDashboardScientificExperimentsData();
                    projectDashboardModule.renderProjectDetailsData();
                }
            }
        },

        refreshPhasesData : function(e) {
            if (e && e.detail && e.detail.reload === true) {
                if (currentTab === 'phases') {
                    phaseModule.renderPhasesData();
                } else if (currentTab === 'dashboard') {
                    projectDashboardModule.renderDashboardPhasesData();
                }
            }
        }
    }

}(jQuery);
