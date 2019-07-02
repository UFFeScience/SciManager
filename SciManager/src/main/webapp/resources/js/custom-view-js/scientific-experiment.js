
var scientificExperimentModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-scientific-experiment-form'));
			sciManagerCommons.initializeChosen($('.chosen-component-input select'));
			scientificExperimentModule.bindUIEvents();
		},

		renderExperimentsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-experiments-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar experimentos científicos',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
				afterAction: function() {
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
			// handle fill create-scientific-experiment form
			$('body').off('click', '#create-scientific-experiment-button')
					 .on('click', '#create-scientific-experiment-button', function() {
				scientificExperimentModule.handleFillCreateScientificExperimentData();
			});

			// submit create-scientific-experiment form
			$('body').off('click', '#create-scientific-experiment-submit-button')
					 .on('click', '#create-scientific-experiment-submit-button', function() {
				scientificExperimentModule.handleCreateScientificExperimentSubmit(this);
			});

			// trigger edit ScientificExperiment modal
			$('body').off('click', '.edit-scientific-experiment')
					 .on('click', '.edit-scientific-experiment', function() {
				scientificExperimentModule.handleFillEditScientificExperimentData(this);
			});

			// handle search select of ScientificProject of edited scientific experiment
			$('body').off('keyup', '.edit-scientific-project .chosen-search input')
					 .on('keyup', '.edit-scientific-project .chosen-search input', function() {

				sciManagerCommons.typewatch(function() {
					if (sciManagerCommons.isValid($('.edit-scientific-project .chosen-search input').val())) {
						var url = sciManagerCommons.getUrlContext(window.location) + '/scientific-project/api/all-scientific-projects?search=' + $('.edit-scientific-project .chosen-search input').val();
						sciManagerCommons.handleChosenSearch('.edit-scientific-project .edit-scientific-project-select', url, 'projectName');
					}
				}, 600);
			});

			// handle edit ScientificExperiment submit
			$('body').off('click', '#edit-scientific-experiment-submit-button')
					 .on('click', '#edit-scientific-experiment-submit-button', function() {
				scientificExperimentModule.handleEditScientificExperimentSubmit();
			});

			// trigger remove ScientificExperiment
			$('body').off('click', '.remove-scientific-experiment-button')
					 .on('click', '.remove-scientific-experiment-button', function() {
				scientificExperimentModule.handleFillRemoveScientificExperimentData(this);
			});

			// on close modal
			$('body').off('hidden.bs.modal', '.bs-remove-scientific-experiment-modal-lg')
					 .on('hidden.bs.modal', '.bs-remove-scientific-experiment-modal-lg', function() {
				$('#dependencies-message').text('');
				$('#delete-scientific-experiment-button').prop('disabled', false);
				$('#deleteScientificExperimentLabel').removeClass('hide');
				$('#canNotDeleteScientificExperimentLabel').addClass('hide');
			});

			// submit remove ScientificExperiment
			$('body').off('click', '#delete-scientific-experiment-button')
					 .on('click', '#delete-scientific-experiment-button', function() {
				scientificExperimentModule.handleRemoveScientificExperimentSubmit();
			});
		},

		handleFillCreateScientificExperimentData : function() {
			var scientificProjectId = $('#experiment-project-id').val();
			$('#project-id').val(scientificProjectId);
		},

		handleCreateScientificExperimentSubmit : function() {
			if (!validator.checkAll($('#create-scientific-experiment-form'))) {
				sciManagerCommons.unlockActionButton($('#create-scientific-experiment-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-experiment/create-scientific-experiment', $('#create-scientific-experiment-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-scientific-experiment-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('experimentRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-scientific-experiment-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar experimento científico.');
						sciManagerCommons.unlockActionButton($('#create-scientific-experiment-submit-button'));
					});
			}
		},

		handleFillEditScientificExperimentData : function(targetElement) {
			var scientificExperimentId = $(targetElement).attr('id').replace('edit-scientific-experiment-', '');
			var experimentName = $('#experiment-name-' + scientificExperimentId).text(),
				projectName = $('#project-name-' + scientificExperimentId).val();

			$('#edit-scientific-experiment-id').val(scientificExperimentId);
			$('#edit-scientific-experiment-name').val(experimentName);
			$('#edit-scientific-project-name').val(projectName);
		},

		handleEditScientificExperimentSubmit : function() {
			if (!validator.checkAll($('#edit-scientific-experiment-form'))) {
				sciManagerCommons.unlockActionButton($('#edit-scientific-experiment-submit-button'));
			}
			else {
				var requestData = {
					scientificExperimentId: $('#edit-scientific-experiment-id').val(),
					experimentName: $('#edit-scientific-experiment-name').val(),
					projectName: $('#project-name-' + $('#edit-scientific-experiment-id').val()).val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-experiment/edit-scientific-experiment', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('#experiment-name-' + requestData.scientificExperimentId).text(requestData.experimentName);
				        	$('.bs-edit-experiment-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#edit-scientific-experiment-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar experimento científico.');
						sciManagerCommons.unlockActionButton($('#edit-scientific-experiment-submit-button'));
					});
			}
		},

		handleFillRemoveScientificExperimentData : function(targetElement) {
			var scientificExperimentId = $(targetElement).attr('id').replace('remove-scientific-experiment-', '');
	    	$('#remove-scientific-experiment-id').val(scientificExperimentId);

	    	$.get(sciManagerCommons.getUrlContext(window.location) + '/scientific-experiment/count-dependencies/' + scientificExperimentId)
				.done(function (data) {

					if (data && data != null && data != undefined && data.workflows > 0) {
						$('#deleteScientificExperimentLabel').addClass('hide');
						$('#canNotDeleteScientificExperimentLabel').removeClass('hide');

						var canNotDeleteMessage = data.workflows + ' workflow(s)';

						$('#dependencies-message').text(canNotDeleteMessage);
						$('#delete-scientific-experiment-button').prop('disabled', true);
					}
				});
		},

		handleRemoveScientificExperimentSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-experiment/remove-scientific-experiment', {scientificExperimentId: $('#remove-scientific-experiment-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-scientific-experiment-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('experimentRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
						sciManagerCommons.unlockActionButton($('#delete-scientific-experiment-button'));
					}

					sciManagerCommons.unlockActionButton($('#delete-scientific-experiment-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover experimento científico.');
					sciManagerCommons.unlockActionButton($('#delete-scientific-experiment-button'));
				});
		}
	}

}(jQuery);
