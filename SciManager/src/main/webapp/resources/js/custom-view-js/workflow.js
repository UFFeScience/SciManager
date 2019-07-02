$(function() {

	workflowModule.init();

});

var workflowModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-workflow-form'));
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
			workflowModule.bindUIEvents();
		},

		renderWorkflowsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-workflows-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar workflows',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
				afterAction: function() {
					sciManagerCommons.initializeChosen($('.chosen-component-input select'));
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.bindPagination(requestData);
			filterSearchCommons.loadData(requestData);
		},

		bindUIEvents : function() {
			// submit create-workflow form
			$('body').off('click', '#create-workflow-submit-button')
					 .on('click', '#create-workflow-submit-button', function() {
				workflowModule.handleCreateWorkflowSubmit(this);
			});

			// handle search select of UserGoup of new Workflow
			$('body').off('keyup', '.user-group .chosen-search input')
					 .on('keyup', '.user-group .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.user-group .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/user-group/api/all-user-goups?search=' + $('.user-group .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.user-group .user-group-select', url, 'groupName');
					}
				}, 600);
			});

			// clear experiments on project change
			$('body').off('change', '.scientific-project .scientific-project-select')
					 .on('change', '.scientific-project .scientific-project-select', function() {
				sciManagerCommons.clearChosenSelects('.scientific-experiment .scientific-experiment-select');
			});

			// handle search select of ScientificProject of new Workflow
			$('body').off('keyup', '.scientific-project .chosen-search input')
					 .on('keyup', '.scientific-project .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.scientific-project .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/scientific-project/api/all-scientific-projects?search=' + $('.scientific-project .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.scientific-project .scientific-project-select', url, 'projectName');
					}
				}, 600);
			});

			// trigger edit Workflow modal
			$('body').off('click', '.edit-workflow').on('click', '.edit-workflow', function() {
				workflowModule.handleFillEditWorkflowData(this);
			});

			// handle search select of userGroups of edited Workflow
			$('body').off('keyup', '.edit-user-group .chosen-search input')
					 .on('keyup', '.edit-user-group .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.edit-user-group .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/user-group/api/all-user-goups?search=' + $('.edit-user-group .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.edit-user-group .edit-user-group-select', url, 'groupName');
					}
				}, 600);
			});

			// handle search select of ScientificProject of edited Workflow
			$('body').off('keyup', '.edit-scientific-project .chosen-search input')
					 .on('keyup', '.edit-scientific-project .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.edit-scientific-project .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/scientific-project/api/all-scientific-projects?search=' + $('.edit-scientific-project .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.edit-scientific-project .edit-scientific-project-select', url, 'projectName');
					}
				}, 600);
			});

			// clear experiments on project change
			$('body').off('change', '.edit-scientific-project .edit-scientific-project-select')
					 .on('change', '.edit-scientific-project .edit-scientific-project-select', function() {
				sciManagerCommons.clearChosenSelects('.edit-scientific-experiment .edit-scientific-experiment-select');
			});

			// handle search select of userGroups of search filter of Workflows
			$('body').off('keyup', '.filter-user-group .chosen-search input')
					 .on('keyup', '.filter-user-group .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.filter-user-group .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/user-group/api/all-user-goups?search=' + $('.filter-user-group .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.filter-user-group .filter-user-group-select', url, 'groupName');
					}
				}, 600);
			});

			// handle search select of ScientificProject of search filter of Workflows
			$('body').off('keyup', '.filter-scientific-project .chosen-search input')
					 .on('keyup', '.filter-scientific-project .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.filter-scientific-project .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/scientific-project/api/all-scientific-projects?search=' + $('.filter-scientific-project .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.filter-scientific-project .filter-scientific-project-select', url, 'projectName');
					}
				}, 600);
			});

			// handle edit workflow submit
			$('body').off('click', '#edit-workflow-submit-button')
					 .on('click', '#edit-workflow-submit-button', function() {
				workflowModule.handleEditWorkflowSubmit();
			});

			// trigger remove workflow
		    $('body').off('click', '.remove-workflow-button')
					 .on('click', '.remove-workflow-button', function() {
				workflowModule.handleFillRemoveWorkflowData(this);
		    });

		    // on close modal
			$('body').off('hidden.bs.modal', '.bs-remove-workflow-modal-lg')
					 .on('hidden.bs.modal', '.bs-remove-workflow-modal-lg', function() {
				$('#dependencies-message').text('');
				$('#delete-workflow-button').prop('disabled', false);
				$('#deleteWorkflowLabel').removeClass('hide');
				$('#canNotDeleteWorkflowLabel').addClass('hide');
			});

		    // submit remove workflow
		    $('body').off('click', '#delete-workflow-button')
					 .on('click', '#delete-workflow-button', function() {
				workflowModule.handleRemoveWorkflowSubmit();
		    });
		},

		handleCreateWorkflowSubmit : function() {
			if (!validator.checkAll($('#create-workflow-form'))) {
				sciManagerCommons.unlockActionButton($('#create-workflow-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/create-workflow', $('#create-workflow-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-workflow-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('workflowRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-workflow-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar workflow.');
						sciManagerCommons.unlockActionButton($('#create-workflow-submit-button'));
					});
			}
		},

		handleFillEditWorkflowData : function(targetElement) {
			var workflowId = $(targetElement).attr('id').replace('edit-workflow-', ''),
				workflowName = $('#workflow-name-' + workflowId).text(),
				swfms = $('#swfms-' + workflowId).text(),
				associatedProjectId = $('#project-id-' + workflowId).val(),
				associatedExperimentId = $('#experiment-id-' + workflowId).val(),
				responsibleGroupName = $('#responsible-group-' + workflowId).text();

			$('#edit-workflow-id').val(workflowId);
			$('#edit-workflow-name').val(workflowName);
			$('#edit-scientific-project-id').val(associatedProjectId);
			$('#edit-scientific-experiment-id').val(associatedExperimentId);
			$('#edit-swfms').val(swfms);

			$('.edit-user-group .edit-user-group-select').empty()
				.append('<option value="' + responsibleGroupName + '" selected>' + responsibleGroupName + '</option>');
			$('.edit-user-group .edit-user-group-select').trigger('chosen:updated');
		},

		handleEditWorkflowSubmit : function() {
			if (!validator.checkAll($('#edit-workflow-form'))) {
				sciManagerCommons.unlockActionButton($('#edit-workflow-submit-button'));
			}
			else {
				var requestData = {
					workflowId: $('#edit-workflow-id').val(),
					workflowName: $('#edit-workflow-name').val(),
					swfms: $('#edit-swfms').val(),
					scientificExperimentId: $('#edit-scientific-experiment-id').val(),
					scientificProjectId: $('#edit-scientific-project-id').val(),
					responsibleGroupName: $('.edit-user-group .edit-user-group-select option:selected').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/edit-workflow', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('#workflow-name-' + requestData.workflowId).text(requestData.workflowName);
							$('#swfms-' + requestData.workflowId).text(requestData.swfms);
							$('#responsible-group-' + requestData.workflowId).text(requestData.responsibleGroupName);
							$('.bs-edit-workflow-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#edit-workflow-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar workflow.');
						sciManagerCommons.unlockActionButton($('#edit-workflow-submit-button'));
					});
			}
		},

		handleFillRemoveWorkflowData : function(targetElement) {
			var workflowId = $(targetElement).attr('id').replace('remove-workflow-', '');
			$('#remove-workflow-id').val(workflowId);

			$.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/count-dependencies/' + workflowId)
				.done(function (data) {
					if (data && data != null && data != undefined && data.tasks > 0) {
						$('#deleteWorkflowLabel').addClass('hide');
						$('#canNotDeleteWorkflowLabel').removeClass('hide');

						var canNotDeleteMessage = data.tasks + ' tarefa(s)';

						$('#dependencies-message').text(canNotDeleteMessage);
						$('#delete-workflow-button').prop('disabled', true);
					}
				});
		},

		handleRemoveWorkflowSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/remove-workflow', {workflowId: $('#remove-workflow-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-workflow-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('workflowRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#delete-workflow-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover workflow.');
					sciManagerCommons.unlockActionButton($('#delete-workflow-button'));
				});
		}
	}

}(jQuery);
