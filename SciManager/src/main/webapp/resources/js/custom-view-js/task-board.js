$(function() {

	taskBoardModule.init();

});

var taskBoardModule = function($) {

	return {

		init : function() {
			taskBoardModule.initializeUiComponents();
			taskBoardModule.bindUIEvents();
		},

		renderTaskBoardData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-task-board-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar tarefas',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
				afterAction: function() {
					taskBoardModule.initializeUiComponents();
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
			// fix sortable jumpy itens in page with scroll
			$('body').css('overflow-x', 'inherit');

			// start taskboard interations
			$('.sortable-list').sortable({
				connectWith: '.connectList',
				stop: taskBoardModule.changeTaskStatusEvent
		    }).disableSelection();
		},

		bindUIEvents : function() {
			// load more tasks of a certain status
			$('body').off('click', '.load-more-tasks').on('click', '.load-more-tasks', function() {
				taskBoardModule.loadMoreTasks(this);
			});
		},

		loadMoreTasks : function(targetElement) {
			var loadMoreButton = $(targetElement);
			loadMoreButton.toggle();

			var scientificProjectId = $('#scientific-project-id').val();
			var status = $(targetElement).attr('id').replace('load-tasks-', '');
			var page = ((+$(targetElement).attr('data-page')) + 1);
			var phaseName = ($('#filter-phase') !== undefined && $('#filter-phase') !== null) ? $('#filter-phase').text() : null;
			var workflowName = ($('#filter-workflow') !== undefined && $('#filter-workflow') !== null) ? $('#filter-workflow').text() : null;
			var search = ($('#filter-name') !== undefined && $('#filter-name') !== null) ? $('#filter-name').text() : null;
			var initialDate = $('.task-initial-date').val();
			var finalDate = $('.task-final-date').val();
			var myOwn = $("#search-form input[type='radio']:checked").val();

			$('#spinner-' + status.toLowerCase()).removeClass('upload-spin-loader-hidden');

			$.get(sciManagerCommons.getUrlContext(window.location) + '/task-board/api/' + scientificProjectId + '?search=' + search + '&status=' + status + '&page=' + page +
																												'&phaseName=' + phaseName + '&workflowName=' + workflowName +
																												'&initialDate=' + initialDate + '&finalDate=' + finalDate +
																												'&myOwn=' + myOwn)
			.done(function (data) {

				if (data.length > 0) {
					$.each(data, function(index) {
						var taskCard = '';

						if (data[index].late === true && status === 'TODO') {
							taskCard += '<li id="card-' + data[index].taskId + '" class="danger-element todo-card">';
						}
						else if (data[index].late === true && status === 'DOING') {
							taskCard += '<li id="card-' + data[index].taskId + '" class="danger-element doing-card">';
						}
						else if (data[index].late !== true && status === 'TODO') {
							taskCard += '<li id="card-' + data[index].taskId + '" class="primary-element todo-card">';
						}
						else if (data[index].late !== true && status === 'DOING') {
							taskCard += '<li id="card-' + data[index].taskId + '" class="info-element doing-card">';
						}
						else {
							taskCard += '<li id="card-' + data[index].taskId + '" class="success-element done-card">';
						}

						taskCard += '<div id="task-details-' + data[index].taskId + '" class="task-left-info">' +
									'<input id="task-late-' + data[index].taskId + '" type="hidden" value="' + data[index].late + '" />' +
									'<div id="task-title-' + data[index].taskId + '" class="task-title">' + data[index].taskTitle + '</div>';


						if (data[index].description !== undefined && data[index].description !== null) {
							taskCard += '<input id="task-description-' + data[index].taskId + '" value="' + data[index].description + '" type="hidden" />';
						}
						else {
							taskCard += '<input id="task-description-' + data[index].taskId + '" value="" type="hidden" />';
						}

						if (data[index].deadline !== undefined && data[index].deadline !== null) {
							taskCard += '<i class="fa fa-clock-o"></i> ' +
										'<span class="task-card-deadline">Limite: ';

							if (data[index].late && status !== 'DONE') {
								taskCard += '<span class="danger-text">' + data[index].deadline + '</span></span> <br />';
							}
							else {
								taskCard += '<span>' + data[index].deadline + '</span></span> <br />';
							}
						}

						taskCard += '<div class="task-card-actions col-xs-12 bottom text-center">' +
									'<div class="emphasis pull-left">' +
									taskBoardModule.buildRunButton(status, data[index]) +
									'<a id="details-button-' + data[index].taskId + '" class="btn-circle details-button btn btn-default btn-xs">' +
									'<i data-toggle="tooltip" data-placement="top" title="Ver detalhes" class="fa fa-search-plus"></i> ' +
									'</a>' +
									'<a id="edit-task-' + data[index].taskId + '" data-toggle="modal" data-target=".bs-edit-task-modal-lg" class="edit-task-button btn btn-info btn-xs">' +
									'<i data-toggle="tooltip" data-placement="top" title="Editar tarefa" class="fa fa-pencil"></i>' +
									'</a>' +
									'<a id="remove-task-' + data[index].taskId  + '" data-toggle="modal" data-target=".bs-remove-task-modal-lg" class="remove-task-button btn btn-danger btn-xs">' +
									'<i data-toggle="tooltip" data-placement="top" title="Deletar" class="fa fa-trash-o"></i> ' +
									'</a>' +
									'</div>' +
									'</div>' +
									'<div class="popover-description-head hide">' +
									'<span class="task-popover-title">Detalhes</span>' +
									'</div>' +
									'<div class="popover-description-content hide">' +
									'<div class="task-popover-content popover-description-container col-md-12 col-sm-12 col-xs-12">' +
									'<b>Criado em: </b> <span>' + data[index].creationDate + '</span> <br />' +
									'<b>Fase: </b> <span id="phase-' + data[index].taskId + '">' + data[index].phase.phaseName + '</span> <br />' +
									'<b>Workflow: </b> <span id="workflow-' + data[index].taskId + '">' + data[index].workflow.workflowName + '</span> <br />';

						if (data[index].description !== undefined && data[index].description !== null) {
							taskCard += '<b>Descrição: </b> <span>' + data[index].description + '</span>';
						}
						else {
							taskCard += '<b>Descrição: </b> <span>--</span>';
						}

						taskCard += '</div></div></div>' +
									'<div class="task-right-info">';

						if (data[index].estimatedTime !== undefined && data[index].estimatedTime !== null) {
							taskCard += '<span class="pull-right task-estimated">Estimativa: <span id="estimated-time-' + data[index].taskId + '" class="badge badge-status">' + data[index].estimatedTime + '</span></span>';
						}

						taskCard += '<div id="users-' + data[index].taskId + '" class="agile-detail pull-right">';

						if (data[index].taskId.userGroupInTask !== undefined && data[index].taskId.userGroupInTask !== null &&
							data[index].taskId.userGroupInTask.groupName !== undefined && data[index].taskId.userGroupInTask.groupName !== null) {

							taskCard += '<div class="alert alert-dismissible responsible-user-info fade in display-inline-block pull-right" role="alert">' +
										'<a id="user-group-' + data[index].taskId + '" class="pull-right">' + data[index].userGroupInTask.groupName + '</a>'+
										'</div><br />';
						}

						if ((data[index].userGroupInTask === undefined || data[index].userGroupInTask === null ||
							data[index].userGroupInTask.groupName === null || data[index].userGroupInTask.groupName === undefined) &&
							data[index].usersInTask !== undefined && data[index].usersInTask !== null &&
							data[index].usersInTask.length > 0) {

							$.each(data[index].usersInTask, function(i, user) {
								taskCard += '<div data-email="' + user.email + '" data-username="' + user.username + '" class="alert alert-dismissible responsible-user-info user-in-task fade in display-inline-block pull-right" role="alert">';

								if (user.profileImage === undefined || user.profileImage === null) {
									taskCard += '<div class="user-group-profile-image">' +
												'<img data-toggle="tooltip" data-placement="top" title="' + user.username + ' (' + data[index].workflow.responsibleGroup.groupName +
												')" src="' + sciManagerCommons.getUrlContext(window.location) + '/resources/images/user.png}" alt="imagem de perfil do usuário" class="avatar" /></div></div>';
								}
								else {
									taskCard += '<div class="user-group-profile-image">' +
												'<img data-toggle="tooltip" data-placement="top" title="' + user.username + ' (' + data[index].workflow.responsibleGroup.groupName +
												')" src="' + user.profileImage + '" alt="imagem de perfil do usuário" class="avatar" /></div></div>';
								}
							});
							taskCard += '<br />';
						}

						if (data[index].tags !== undefined && data[index].tags !== null && data[index].tags.length > 0) {
							taskCard += '<div id="tags-' + data[index].taskId+ '" class="user-task-tags">';

							$.each(data[index].tags, function(i, tag) {
								taskCard += '<span class="task-tag pull-right label label-primary">' + tag.tagName + '</span>';
							});
							taskCard += '<br /></div>';
						}

						taskCard += '</div></div></li>';

						$('.' + status.toLowerCase() + '-tasks-loaded').text((+$('.' + status.toLowerCase() + '-tasks-loaded').text()) + data.length);
						$('.' + status.toLowerCase() + '-box .sortable-list').append(taskCard);

						popoverCommons.buildDetailsPopover();
						loadMoreButton.attr('data-page', page);

						if ((+$('.' + status.toLowerCase() + '-tasks-loaded').text()) < (+$('.' + status.toLowerCase() + '-total-tasks').text())) {
							loadMoreButton.toggle();
						}
					});
				}
			})
			.fail(function() {
				sciManagerCommons.buildErrorMessage('Erro ao carregar tarefas.');
			});

			$('#spinner-' + status.toLowerCase()).addClass('upload-spin-loader-hidden');
		},

		buildRunButton : function(status, task) {
			return task.phase.allowExecution === 'true' ?
					('<a id="run-button-' + task.taskId + '" data-toggle="modal" data-target=".bs-run-task-workflow-modal-lg" class="btn-circle run-task-workflow-button ' + (status !== 'DOING' ? 'run-blocked ' : '') +  'btn btn-default btn-xs">' +
					'<i data-toggle="tooltip" data-placement="top" title="Executar Workflow" class="run-workflow-button fa fa-play"></i>' +
					'</a>') : '';
		},

		changeTaskStatusEvent : function(event, ui) {
			var newStatus;
			var statusDestinyClass;
			var statusOriginClass;

			if (($(ui.item).hasClass('todo-card') && !$(ui.item).parents('.todo-box').length) ||
				($(ui.item).hasClass('doing-card') && !$(ui.item).parents('.doing-box').length) ||
				($(ui.item).hasClass('done-card') && !$(ui.item).parents('.done-box').length) ) {

				if ($(ui.item).hasClass('todo-card')) {
					statusOriginClass = 'todo';
				}
				else if ($(ui.item).hasClass('doing-card')) {
					statusOriginClass = 'doing';
				}
				else {
					statusOriginClass = 'done';
				}

				if ($(ui.item).parents('.doing-box').length) {
					newStatus = 'DOING';
					statusDestinyClass = 'doing';
				}
				else if ($(ui.item).parents('.done-box').length) {
					newStatus = 'DONE';
					statusDestinyClass = 'done';
				}
				else {
					newStatus = 'TODO';
					statusDestinyClass = 'todo';
				}

				if (newStatus && newStatus !== null && newStatus !== undefined) {
					var taskId = $(ui.item).attr('id').replace('card-', '');
					$.post(sciManagerCommons.getUrlContext(window.location) + '/task-board/edit-task-status', {taskId: taskId, newStatus: newStatus})
						.done(function(data) {
							if (sciManagerCommons.isSuccessType(data.type)) {

								$(ui.item).removeClass('todo-card');
								$(ui.item).removeClass('doing-card');
								$(ui.item).removeClass('done-card');

								$(ui.item).removeClass('primary-element');
								$(ui.item).removeClass('info-element');
								$(ui.item).removeClass('success-element');
								$(ui.item).removeClass('danger-element');

								$('.' + statusOriginClass + '-tasks-loaded').text((+$('.' + statusOriginClass + '-tasks-loaded').text()) - 1);
								$('.' + statusDestinyClass + '-tasks-loaded').text((+$('.' + statusDestinyClass + '-tasks-loaded').text()) + 1);

								var late = $('#task-late-' + taskId).val();

								if (newStatus === 'TODO') {
									$(ui.item).addClass('todo-card');
									$(ui.item).find('.run-task-workflow-button').addClass('run-blocked');

									if (late !== 'true') {
										$(ui.item).addClass('primary-element');
									}
									else {
										$(ui.item).addClass('danger-element');
									}

									var deadlineElement = $(ui.item).find('.task-card-deadline');
									if (deadlineElement && deadlineElement !== null && !deadlineElement !== undefined) {
										if (late === 'true') {
											deadlineElement.find('span').addClass('danger-text');
										}
									}
								}
								else if (newStatus === 'DOING') {
									$(ui.item).addClass('doing-card');
									$(ui.item).find('.run-task-workflow-button').removeClass('run-blocked');

									if (late !== 'true') {
										$(ui.item).addClass('info-element');
									}
									else {
										$(ui.item).addClass('danger-element');
									}

									var deadlineElement = $(ui.item).find('.task-card-deadline');
									if (deadlineElement && deadlineElement !== null && !deadlineElement !== undefined) {
										if (late === 'true') {
											deadlineElement.find('span').addClass('danger-text');
										}
									}
								}
								else {
									$(ui.item).addClass('done-card');
									$(ui.item).addClass('success-element');
									$(ui.item).find('.run-task-workflow-button').addClass('run-blocked');

									var deadlineElement = $(ui.item).find('.task-card-deadline');
									if (deadlineElement && deadlineElement !== null && !deadlineElement !== undefined) {
										deadlineElement.find('span').removeClass('danger-text');
									}
								}
								sciManagerCommons.buildSuccessMessage(data.text);
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
	}

}(jQuery);
