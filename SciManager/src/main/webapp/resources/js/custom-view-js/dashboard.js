$(function() {

    dashboardModule.init();

});

var dashboardModule = function($) {

    return {

        init : function() {
            dashboardModule.renderUserProjectsData();
            dashboardModule.renderUserWorkflowsData();
            dashboardModule.renderUserTasksData();

        },

        renderUserProjectsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-projects-url').val(),
                errorMessage: 'Erro ao buscar projetos do usuário',
                container: '.projects-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.projects-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.projects-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.projects-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        },

        renderUserWorkflowsData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-workflows-url').val(),
                errorMessage: 'Erro ao buscar workflows do usuário',
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
        },

        renderUserTasksData : function() {
            var requestData = {
                url: sciManagerCommons.getUrlContext(window.location) + $('#search-tasks-url').val(),
                errorMessage: 'Erro ao buscar tarefas do usuário',
                container: '.tasks-container',
                afterAction: function() {
                    sciManagerCommons.loaded('.tasks-container .loading-data-async');
                },
                beforeAction: function() {
                    sciManagerCommons.loading('.tasks-container .loading-data-async');
                }
            };

            sciManagerCommons.loading('.tasks-container .loading-data-async');
            filterSearchCommons.loadData(requestData);
        }
    }

}(jQuery);
