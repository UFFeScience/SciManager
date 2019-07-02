$(function() {

	tasksCommons.init();

});

var tasksCommons = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-task-form'));
			sciManagerCommons.bindFormValidation($('#edit-task-form'));
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
			tasksCommons.bindUIEvents();
		},

		initializeDataComponents : function() {
			$('#scientific-project-id-attribute').val($('#scientific-project-id').val());
			$('#user-creator-id-attribute').val($('#user-creator-id').val());

			// start tagIt
			sciManagerCommons.initializeTagIt('#task-tags');
			sciManagerCommons.initializeTagIt('#edit-task-tags');
		},

		bindUIEvents : function() {
			// special mask and validation for estimated days of task
			$('body').off('keypress', '#estimated-days-input')
					 .on('keypress', '#estimated-days-input', function(e) {
				tasksCommons.addEstimatedDaysValidation(e, this);
			});

			// trigger run workflow of task
		    $('body').off('click', '.run-task-workflow-button')
					 .on('click', '.run-task-workflow-button', function() {
				tasksCommons.handleTriggerRunWorkflow(this);
		    });

		    // submit command to run workflow of task
		    $('body').off('click', '#run-task-workflow-button')
					 .on('click', '#run-task-workflow-button', function() {
				tasksCommons.handleRunWorkflow();
		    });

		    // trigger remove task
		    $('body').off('click', '.remove-task-button')
					 .on('click', '.remove-task-button', function() {
				tasksCommons.handleTriggerRemoveTask(this);
		    });

		    // trigger edit task modal
		    $('body').off('click', '.edit-task-button')
					 .on('click', '.edit-task-button', function() {
				tasksCommons.handleTriggerEditTask(this);
		    });

		    // set group responsible for new task
		    $('body').off('change', '.workflow-select')
					 .on('change', '.workflow-select', function() {
				tasksCommons.setResponsibleGroupForNewTask();
		    });

		    // set group responsible for task in edit modal
		    $('body').off('change', '.edit-workflow-select')
					 .on('change', '.edit-workflow-select', function() {
		    	tasksCommons.setResponsibleGroupForEditedTask();
		    });

			// handle check group checkbox
		    $('body').off('ifChecked', '#group')
					 .on('ifChecked', '#group', function() {
				tasksCommons.handleCheckGroupCreateTask();
		    });

		    // handle check group checkbox of edit modal
		    $('body').off('ifChecked', '#edit-group')
					 .on('ifChecked', '#edit-group', function() {
				tasksCommons.handleCheckGroupEditTask();
		    });

		    // handle uncheck group checkbox
		    $('body').off('ifUnchecked', '#group')
					 .on('ifUnchecked', '#group', function() {
				tasksCommons.handleUncheckGroupCreateTask();
		    });

		    // handle uncheck group checkbox of edit modal
		    $('body').off('ifUnchecked', '#edit-group')
					 .on('ifUnchecked', '#edit-group', function() {
				tasksCommons.handleUncheckGroupEditTask();
		    });

		    // handle check users checkbox of edit modal
		    $('body').off('ifChecked', '#edit-users')
					 .on('ifChecked', '#edit-users', function() {
				tasksCommons.handleCheckUsersEditTask();
		    });

		    // handle check users checkbox
		    $('body').off('ifChecked', '#users')
					 .on('ifChecked', '#users', function() {
				tasksCommons.handleCheckUsersCreateTask();
		    });

		    // handle uncheck users checkbox
		    $('body').off('ifUnchecked', '#users')
					 .on('ifUnchecked', '#users', function() {
				tasksCommons.handleUncheckUsersCreateTask();
		    });

		    // handle uncheck users checkbox of edit modal
		    $('body').off('ifUnchecked', '#edit-users')
					 .on('ifUnchecked', '#edit-users', function() {
				tasksCommons.handleUncheckUsersEditTask();
		    });

			// submit create task
		    $('body').off('click', '#create-task-submit-button')
					.on('click', '#create-task-submit-button', function() {
				tasksCommons.handleCreateTaskSubmit();
		    });

		    // submit edit task
		    $('body').off('click', '#edit-task-submit-button')
					 .on('click', '#edit-task-submit-button', function() {
		    	tasksCommons.handleEditTaskSubmit();
		    });

			// submit remove task
		    $('body').off('click', '#delete-task-button')
					 .on('click', '#delete-task-button', function() {
		    	tasksCommons.handleRemoveTaskSubmit();
		    });
		},

		addEstimatedDaysValidation : function(e, targetElement) {
			var textValue = $(targetElement).val();
			var numericValue = +textValue;

			if (numericValue >= 1000) {
				e.preventDefault();
				return false;
			}

			if (e.which < 48 || e.which > 57) {
				e.preventDefault();
				return false;
			}
		},

		handleTriggerRunWorkflow : function(targetElement) {
			if (!$(targetElement).hasClass('run-blocked')) {
				$('.bs-run-task-workflow-modal-lg').modal('show');
			}
			var taskId = $(targetElement).attr('id').replace('run-button-', '');
			var workflowId = $('#task-workflow-id-' + taskId).val();

			$.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/count-executions?workflowId=' + workflowId)
				 .done(function (data) {
					 if (+data <= 0) {
						 $('#run-task-id').val(taskId);
						 $('#runTaskWorkflowLabel').text('Deseja mesmo executar o workflow associado a esta tarefa?');
						 $('.run-task-modal-footer').removeClass('hide');
					 }
					 else {
						 $('#runTaskWorkflowLabel').text('Já há uma execução corrente para essa versão do workflow.');
						 $('.run-task-modal-footer').addClass('hide');
					 }
				 })
				 .fail(function () {}
				 );
		},

		handleRunWorkflow : function() {
			var taskId = $('#run-task-id').val();
			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/process-execution', {taskId: taskId})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						sciManagerCommons.buildSuccessMessage(data.text);
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
					$(".bs-run-task-workflow-modal-lg .close").click();
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro realizar execução do workflow.');
					$(".bs-run-task-workflow-modal-lg .close").click();
				});
		},

		handleTriggerRemoveTask : function(targetElement) {
			var taskId = $(targetElement).attr('id').replace('remove-task-', '');
			$('#remove-task-id').val(taskId);
		},

		handleTriggerEditTask : function(targetElement) {
			var taskId = $(targetElement).attr('id').replace('edit-task-', '');

			$('#edit-task-id').val(taskId);
			$('#edit-task-title').val($('#task-title-' + taskId).text());
			$('#edit-description').val($('#task-description-' + taskId).val());
			$('#edit-url-repository').val($('#task-url-repository-' + taskId).val());
			$('#edit-estimated-time').val($('#estimated-time-' + taskId) !== null ? $('#estimated-time-' + taskId).text() : null);
			$('#edit-deadline').val(($('#deadline-' + taskId).text() !== null && $('#deadline-' + taskId).text() !== '--') ? $('#deadline-' + taskId).text() : null);

			$('#edit-task-phase-name').val($('#phase-' + taskId).text());
			$('#edit-task-phase-name').trigger('chosen:updated');

			$('#edit-workflow-name').val($('#workflow-' + taskId).text());
			$('#edit-workflow-name').trigger('chosen:updated');

			tasksCommons.setResponsibleGroupForEditedTask();
			tasksCommons.setTagsOfTask(taskId);
			tasksCommons.setResponsibleOfTask(taskId);
		},

		setTagsOfTask : function(taskId) {
			$('#edit-task-tags').tagit('removeAll');
			$('#tags-' + taskId + ' span').each(function() {
				$('#edit-task-tags').tagit('createTag', $(this).text());
			});
		},

		setResponsibleOfTask : function(taskId) {
			var groupName = $('#user-group-' + taskId).text();
			if (groupName && groupName !== null) {
				$('#edit-group').iCheck('check');
			}
			else {
				var users = [];
				$('#users-' + taskId + ' .responsible-user-info').each(function() {
					users.push($(this).attr('data-email'));
				});

				if (users.length !== 0) {
					$('#edit-users').iCheck('check');
					$('.edit-usernames-select').val(users);
					$('.edit-usernames-select').trigger('chosen:updated');
				}
			}
		},

		setResponsibleGroupForNewTask : function() {
			var userGroupInTask = $('.workflow-select option:selected').attr('data-group');

			if (userGroupInTask) {
				$('.responsible-group').text('Grupo (' + userGroupInTask + '):');
			}
			else {
				$('.responsible-group').text('Grupo:');
			}

			$('.add-usernames-select').empty().append('<option value=""></option>');
			$('.add-usernames-select').trigger('chosen:updated');
			$('#user-group-in-task').val('');

			$('#users').iCheck('uncheck');
			$('#group').iCheck('uncheck');
		},

		handleCheckGroupCreateTask : function() {
			if ($('#group').is(":checked")) {
				$('#users').iCheck('uncheck');

				var userGroupInTask = $('.workflow-select option:selected').attr('data-group');
				$('.add-usernames-select').empty().append('<option value=""></option>');
				$('.add-usernames-select').trigger('chosen:updated');

				$('#user-group-in-task').val(userGroupInTask);
			}
		},

		handleCheckGroupEditTask : function() {
			if ($('#edit-group').is(":checked")) {
				$('#edit-users').iCheck('uncheck');

				var userGroupInTask = $('.edit-workflow-select option:selected').attr('data-group');
				$('.edit-usernames-select').empty().append('<option value=""></option>');
				$('.edit-usernames-select').trigger('chosen:updated');

				$('#edit-user-group-in-task').val(userGroupInTask);
			}
		},

		handleUncheckGroupCreateTask : function() {
			if (!$('#group').is(":checked")) {
				$('#user-group-in-task').val('');
			}
		},

		handleUncheckGroupEditTask : function() {
			if (!$('#edit-group').is(":checked")) {
				$('#edit-user-group-in-task').val('');
			}
		},

		handleCheckUsersEditTask : function() {
			$('#edit-user-group-in-task').val('');
			$('#edit-responsible-users').slideDown();

			if ($('#edit-users').is(":checked")) {
				$('#edit-group').iCheck('uncheck');
				$('.edit-usernames-select').empty();

				var workflowId =  $('.edit-workflow-select option:selected').attr('data-workflow-id');

				if (workflowId) {
					$('#workflow-users-select-' + workflowId + ' option').each(function() {
						$('.edit-usernames-select')
							.append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
					});
				}
				else {
					$('#project-users-select option').each(function() {
						$('.edit-usernames-select')
							.append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
					});
				}

				$('.edit-usernames-select').trigger('chosen:updated');
			}
		},

		handleCheckUsersCreateTask : function() {
			$('#user-group-in-task').val('');
			$('#responsible-users').slideDown();

			if ($('#users').is(":checked")) {
				$('#group').iCheck('uncheck');
				$('.add-usernames-select').empty();

				var workflowId =  $('.workflow-select option:selected').attr('data-workflow-id');

				if (workflowId) {
					$('#workflow-users-select-' + workflowId + ' option').each(function() {
						$('.add-usernames-select')
							.append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
					});
				}
				else {
					$('#project-users-select option').each(function() {
						$('.add-usernames-select')
							.append('<option value="' + $(this).val() + '">' + $(this).text() + '</option>');
					});
				}

				$('.add-usernames-select').trigger('chosen:updated');
			}
		},

		handleUncheckUsersCreateTask : function() {
			$('#responsible-users').slideUp();
			$('.add-usernames-select').empty().append('<option value=""></option>');
			$('.add-usernames-select').trigger('chosen:updated');
		},

		handleUncheckUsersEditTask : function() {
			$('#edit-responsible-users').slideUp();
			$('.edit-usernames-select').empty().append('<option value=""></option>');
			$('.edit-usernames-select').trigger('chosen:updated');
		},

		setResponsibleGroupForEditedTask : function() {
	    	var userGroupInTask = $('.edit-workflow-select option:selected').attr('data-group');

			if (userGroupInTask) {
				$('.edit-responsible-group').text('Grupo (' + userGroupInTask + '):');
			}
			else {
				$('.edit-responsible-group').text('Grupo:');
			}

	    	$('.edit-usernames-select').empty().append('<option value=""></option>');
			$('.edit-usernames-select').trigger('chosen:updated');

			$('#edit-users').iCheck('uncheck');
			$('#edit-group').iCheck('uncheck');
	    },

        handleCreateTaskSubmit : function() {
			if (!validator.checkAll($('#create-task-form'))) {
				sciManagerCommons.unlockActionButton($('#create-task-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/task-board/create-task', $('#create-task-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-task-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('taskRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar tarefa.');
					});
			}
		},

        handleEditTaskSubmit : function() {
			if (!validator.checkAll($('#edit-task-form'))) {
				sciManagerCommons.unlockActionButton($('#edit-task-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/task-board/edit-task', $('#edit-task-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-edit-task-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('taskRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar tarefa.');
					});
			}
		},

        handleRemoveTaskSubmit : function(renderCallback) {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/task-board/remove-task', {taskId: $('#remove-task-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-task-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('taskRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover tarefa.');
				});
		}
	}

}(jQuery);
