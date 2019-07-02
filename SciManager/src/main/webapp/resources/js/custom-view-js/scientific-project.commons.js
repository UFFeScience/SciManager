$(function() {

	scientificProjectCommons.init();

});

var scientificProjectCommons = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-scientific-project-form'));
			sciManagerCommons.bindFormValidation($('#edit-scientific-project-form'));
			scientificProjectCommons.bindUIEvents();
		},

		bindUIEvents: function() {
			// submit create-scientific-project form
			$('body').off('click', '#create-scientific-project-submit-button')
					 .on('click', '#create-scientific-project-submit-button', function() {
				scientificProjectCommons.handleCreateScientificProjectSubmit(this);
			});

			// trigger edit scientific project modal
			$('body').off('click', '.edit-scientific-project')
					 .on('click', '.edit-scientific-project', function() {
				scientificProjectCommons.handleFillEditScientificProjectData(this);
			});

			// handle edit scientific project submit
			$('body').off('click', '#edit-scientific-project-name-submit-button')
					 .on('click', '#edit-scientific-project-name-submit-button', function() {
				scientificProjectCommons.handleEditScientificProjectSubmit();
			});

			// trigger remove scientific project
		    $('body').off('click', '.remove-scientific-project-button')
					 .on('click', '.remove-scientific-project-button', function() {
				scientificProjectCommons.handleFillRemoveScientificProjectData(this);
		    });

		    // submit remove scientific project
		    $('body').off('click', '#delete-scientific-project-button')
					 .on('click', '#delete-scientific-project-button', function() {
				scientificProjectCommons.handleRemoveScientificProjectSubmit();
		    });
		},

		handleCreateScientificProjectSubmit : function() {
			if (!validator.checkAll($('#create-scientific-project-form'))) {
				sciManagerCommons.unlockActionButton($('#create-scientific-project-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/create-scientific-project', $('#create-scientific-project-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-project-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							sciManagerCommons.publishEvent('projectsRefresh', {'detail': {'reload': true}});
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar projeto científico.');
					});
			}
		},

		handleFillEditScientificProjectData : function(targetElement) {
			var scientificProjectId = $(targetElement).attr('id').replace('edit-scientific-project-', '');
			var projectName = $('#project-name-' + scientificProjectId).text();

			$('#edit-scientific-project-id').val(scientificProjectId);
			$('#edit-scientific-project-name').val(projectName);
		},

		handleFillRemoveScientificProjectData : function(targetElement) {
			var scientificProjectId = $(targetElement).attr('id').replace('remove-scientific-project-', '');
			$('#remove-scientific-project-id').val(scientificProjectId);
		},

		handleRemoveScientificProjectSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/remove-scientific-project', {scientificProjectId: $('#remove-scientific-project-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-scientific-project-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('projectsRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#delete-scientific-project-button'));
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover projeto científico.');
					sciManagerCommons.unlockActionButton($('#delete-scientific-project-button'));
				});
		},

		handleEditScientificProjectSubmit : function() {
			if (!validator.checkAll($('#edit-scientific-project-form'))) {
				sciManagerCommons.unlockActionButton($('#edit-scientific-project-name-submit-button'));
			}
			else {
				var requestData = {
					scientificProjectId: $('#edit-scientific-project-id').val(),
					projectName: $('#edit-scientific-project-name').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/edit-scientific-project', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('#project-name-' + requestData.scientificProjectId).text(requestData.projectName);
							$('.bs-edit-project-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}

						sciManagerCommons.unlockActionButton($('#edit-scientific-project-name-submit-button'));
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar nome do projeto científico.');
						sciManagerCommons.unlockActionButton($('#edit-scientific-project-name-submit-button'));
					});
			}
		}
	}

}(jQuery);
