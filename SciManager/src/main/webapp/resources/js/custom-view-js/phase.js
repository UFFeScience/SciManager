$(function() {

	phaseModule.init();

});

var phaseModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-phase-form'));
			phaseModule.bindUIEvents();
		},

		renderPhasesData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-phases-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar fases',
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

		bindUIEvents: function() {
			// open create-phase modal
			$('body').off('click', '#create-phase-button')
					 .on('click', '#create-phase-button', function() {
				phaseModule.handleFillCreatePhaseData(this);
			});

			// submit create-phase form
			$('body').off('click', '#create-phase-submit-button')
					 .on('click', '#create-phase-submit-button', function() {
				phaseModule.handleCreatePhaseSubmit(this);
			});

			// trigger edit phase modal
			$('body').off('click', '.edit-phase')
					 .on('click', '.edit-phase', function() {
				phaseModule.handleFillEditPhaseData(this);
			});

			// handle edit phase submit
			$('body').off('click', '#edit-phase-submit-button')
					 .on('click', '#edit-phase-submit-button', function() {
				phaseModule.handleEditPhaseSubmit();
			});

			// trigger remove phase
		    $('body').off('click', '.remove-phase-button')
					 .on('click', '.remove-phase-button', function() {
				phaseModule.handleFillRemovePhaseData(this);
		    });

		    // submit remove phase
		    $('body').off('click', '#delete-phase-button')
					 .on('click', '#delete-phase-button', function() {
				phaseModule.handleRemovePhaseSubmit();
		    });

			// on close remove phase modal
			$('body').off('hidden.bs.modal', '.bs-remove-phase-modal-lg')
					 .on('hidden.bs.modal', '.bs-remove-phase-modal-lg', function() {
				$('#dependencies-message').text('');
				$('#delete-phase-button').prop('disabled', false);
				$('#deletePhaseLabel').removeClass('hide');
				$('#canNotDeletePhaseLabel').addClass('hide');
			});
		},

		handleFillCreatePhaseData : function() {
			var scientificProjectId = $('#scientific-project-id').val();
			$('#phase-project-id').val(scientificProjectId);
		},

		handleCreatePhaseSubmit : function() {
			if (!validator.checkAll($('#create-phase-form'))) {
				sciManagerCommons.unlockActionButton($('#create-phase-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/phase/create-phase', $('#create-phase-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-phase-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('phaseRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#create-phase-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar fase.');
						sciManagerCommons.unlockActionButton($('#create-phase-submit-button'));
					});
			}
		},

		handleFillEditPhaseData : function(targetElement) {
			var phaseId = $(targetElement).attr('id').replace('edit-phase-', ''),
				phaseName = $('#phase-name-' + phaseId).text(),
				allowExecution = $('#allow-execution-value-' + phaseId).val();

			$('#edit-phase-id').val(phaseId);
			$('#edit-phase-name').val(phaseName);

			if (allowExecution === 'true') {
				$('#edit-allow-execution-true').click();
			}
			else {
				$('#edit-allow-execution-false').click();
			}
		},

		handleFillRemovePhaseData : function(targetElement) {
			var phaseId = $(targetElement).attr('id').replace('remove-phase-', '');
	    	$('#remove-phase-id').val(phaseId);

	    	$.get(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/phase/count-dependencies/' + phaseId)
				.done(function (data) {

					if (data && data != null && data != undefined && data.tasks > 0) {
						$('#deletePhaseLabel').addClass('hide');
						$('#canNotDeletePhaseLabel').removeClass('hide');

						var canNotDeleteMessage = data.tasks + ' tarefas';

						$('#dependencies-message').text(canNotDeleteMessage);
						$('#delete-phase-button').prop('disabled', true);
					}
				});
		},

		handleRemovePhaseSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/phase/remove-phase', {phaseId: $('#remove-phase-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-phase-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('phaseRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#remove-phase-form'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover fase.');
					sciManagerCommons.unlockActionButton($('#remove-phase-form'));
				});
		},

		handleEditPhaseSubmit : function() {
			if (!sciManagerCommons.isValid($('#edit-phase-name').val())) {
				$('#edit-phase-name').trigger('blur');
				sciManagerCommons.unlockActionButton($('#edit-phase-submit-button'));
			}
			else {
				var data = {
					phaseId: $('#edit-phase-id').val(),
					phaseName: $('#edit-phase-name').val(),
					scientificProjectId: $('#edit-project-id').val(),
					allowExecution: $('.allow-execution-btn.active input').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/phase/edit-phase', data)
			        .done(function(responseData) {
			        	if (sciManagerCommons.isSuccessType(responseData.type)) {
			        		$('#phase-name-' + data.phaseId).text(data.phaseName);
			        		$('#allow-execution-value-' + data.phaseId).val(data.allowExecution);

			        		if (data.allowExecution === 'true') {
			        			$('#allow-execution-label-' + data.phaseId).text('Sim');
			        		}
			        		else {
			        			$('#allow-execution-label-' + data.phaseId).text('Não');
			        		}

				        	$('.bs-edit-phase-modal-lg').modal('hide');
				            sciManagerCommons.buildSuccessMessage(responseData.text);
							sciManagerCommons.unlockActionButton($('#edit-phase-submit-button'));
			        	}
			        	else {
			        		sciManagerCommons.buildErrorMessage(responseData.text);
							sciManagerCommons.unlockActionButton($('#edit-phase-submit-button'));
			        	}
			        })
			    	.fail(function(data) {
			    		sciManagerCommons.buildErrorMessage('Erro ao editar nome da fase.');
						sciManagerCommons.unlockActionButton($('#edit-phase-submit-button'));
			    	});
			}
		}
	}

}(jQuery);
