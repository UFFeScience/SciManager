$(function() {

	taskListModule.init();

});

var taskListModule = function($) {

	return {

		init : function() {
			taskListModule.initializeUiComponents();
			taskListModule.bindUIEvents();
		},

		renderTaskListData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-tasks-list-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar tarefas',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
				afterAction: function() {
					taskListModule.initializeUiComponents();
					sciManagerCommons.initializeDateComponents();
					sciManagerCommons.initializeChosen($('.chosen-component-input select'));
					tasksCommons.initializeDataComponents();
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.bindPagination(requestData);
			filterSearchCommons.loadData(requestData);
		},

		initializeUiComponents : function() {
			// trigger edit status
		    $('body').find('.btn-status').each(function() {
		    	var taskId = $(this).attr('id').replace('status-button-', '');
		    	$(this).popover({
		    		html: true,
		    		container: 'body',
		    		mode: 'popup',
		    		placement: 'top',
		    		title: function() {
		    			return $('#status-td-' + taskId + ' .popover-head').html();
		    		},
		    		content: function() {
		    			return $('#status-td-' + taskId + ' .popover-content').html();
		    		}
		    	}).on('shown.bs.popover', function() {
		    		var taskId = $(this).attr('id').replace('status-button-', '');
		        	$('.popover .status-select').val($('#task-status-helper-'+ taskId).val());

		    	});
		    });
		},

		bindUIEvents : function() {
			// trigger change status select
		    $(document).off('change', '.status-select')
					   .on('change', '.status-select', function() {
		    	$('.status-select').val(($(this).val()));
		    });

		    // update task status
		    $('body').off('click', '.update-status-button')
					 .on('click', '.update-status-button', function() {
				taskListModule.handleUpdateTaskStatus(this);
			});
		},

		handleUpdateTaskStatus : function(targetElement) {
			var taskId = $(targetElement).attr('id').replace('update-status-', '');
			var newStatus = $('.popover .status-select').val();
			var newStatusText = $('.popover .status-select option:selected').text();

			if (!newStatus || !taskId) {
				sciManagerCommons.buildErrorMessage('Erro ao editar status de tarefa.');
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/task-board/edit-task-status', {taskId: taskId, newStatus: newStatus})
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {

							if (newStatus === 'TODO') {
								if (!$('#status-button-' + taskId).hasClass('status-late')) {
									$('#status-button-' + taskId).removeClass('status-doing');
									$('#status-button-' + taskId).removeClass('status-done');
									$('#status-button-' + taskId).removeClass('btn-info');
									$('#status-button-' + taskId).removeClass('btn-success');

									if ($('#task-late-' + taskId).val() === 'true') {
										$('#deadline-' + taskId).addClass('danger-text');
										$('#status-button-' + taskId).removeClass('btn-primary');
										$('#status-button-' + taskId).removeClass('status-to-do');
										$('#status-button-' + taskId).addClass('btn-danger');
										$('#status-button-' + taskId).addClass('status-late');
									}
									else {
										$('#status-button-' + taskId).addClass('btn-primary');
										$('#status-button-' + taskId).addClass('status-to-do');
									}
								}
								$('#run-button-' + taskId).addClass('run-blocked');
								$('#status-button-' + taskId).text(newStatusText);
							}
							else if (newStatus === 'DOING') {
								if (!$('#status-button-' + taskId).hasClass('status-late')) {
									$('#status-button-' + taskId).removeClass('status-to-do');
									$('#status-button-' + taskId).removeClass('status-done');
									$('#status-button-' + taskId).removeClass('btn-primary');
									$('#status-button-' + taskId).removeClass('btn-success');

									if ($('#task-late-' + taskId).val() === 'true') {
										$('#deadline-' + taskId).addClass('danger-text');
										$('#status-button-' + taskId).removeClass('btn-info');
										$('#status-button-' + taskId).removeClass('status-doing');
										$('#status-button-' + taskId).addClass('btn-danger');
										$('#status-button-' + taskId).addClass('status-late');
									}
									else {
										$('#status-button-' + taskId).addClass('btn-info');
										$('#status-button-' + taskId).addClass('status-doing');
									}
								}
								$('#run-button-' + taskId).removeClass('run-blocked');
								$('#status-button-' + taskId).text(newStatusText);
							}
							else {
								$('#status-button-' + taskId).removeClass('status-late');
								$('#status-button-' + taskId).removeClass('status-doing');
								$('#status-button-' + taskId).removeClass('status-to-do');
								$('#status-button-' + taskId).removeClass('btn-primary');
								$('#status-button-' + taskId).removeClass('btn-info');
								$('#status-button-' + taskId).removeClass('btn-danger');
								$('#deadline-' + taskId).removeClass('danger-text');

								$('#status-button-' + taskId).addClass('status-done');
								$('#status-button-' + taskId).addClass('btn-success');
								$('#status-button-' + taskId).text(newStatusText);

								$('#run-button-' + taskId).addClass('run-blocked');
							}

							$('#task-status-helper-'+ taskId).val($('.popover .status-select').val());
							popoverCommons.hidePopovers();
							sciManagerCommons.buildSuccessMessage(data.text);
							taskListModule.renderTaskListData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar status.');
					});
			}
		}
	}

}(jQuery);
