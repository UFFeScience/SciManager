$(function() {

	modelFileModule.init();

});

var modelFileModule = function($) {

	return {

		init : function() {
			modelFileModule.bindUIEvents();
		},

		renderModelFilesData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-model-files-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar arquivos modelo',
				paginationSelector: '.page-link:not(.disabled)',
				paramsToKeep: ['tab'],
				afterAction: function() {
					sciManagerCommons.initializeDateComponents();
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
			// on close remove file modal
			$('body').off('hidden.bs.modal', '.bs-remove-model-file-modal-lg')
					 .on('hidden.bs.modal', '.bs-remove-model-file-modal-lg', function() {
				$('#delete-model-file-button').prop('disabled', false);
				$('#deleteModelFileLabel').removeClass('hide');
				$('#canNotDeleteModelFileLabel').addClass('hide');
			});

			// trigger file comparison
			$('body').off('click', '.compare-files-button').on('click', '.compare-files-button', function() {
				sciManagerCommons.loading();
				modelFileModule.handleCompareFilesData(this);
			});

			// trigger hide loading icon for comparison modal
			$('body').off('shown.bs.modal', '.bs-comparison-file-modal-lg')
					 .on('shown.bs.modal', '.bs-comparison-file-modal-lg', function(e) {

				if ($(e.target).hasClass('bs-comparison-file-modal-lg')) {
					sciManagerCommons.loaded();
				}
			});

			// trigger submit alter current file
			$('body').off('click', '#alter-current-file-button')
					 .on('click', '#alter-current-file-button', function() {
				modelFileModule.handleAlterModelFileSubmit();
			});

			// trigger remove model file
		    $('body').off('click', '.remove-model-file-button')
					 .on('click', '.remove-model-file-button', function() {
				modelFileModule.handleFillRemoveFileModelData(this);
		    });

		    // submit remove model file
		    $('body').off('click', '#delete-model-file-button')
					 .on('click', '#delete-model-file-button', function() {
				modelFileModule.handleRemoveModelFileSubmit();
		    });
		},

		handleAlterModelFileSubmit : function() {
			var data = {
				modelFileId: $('.alter-file-model-file-id').val(),
				workflowId: $('.alter-file-workflow-id').val()
			};
			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/model-file/alter-current-file', data)
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-comparison-file-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('modelFileRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao setar novo arquivo modelo.');
				});
		},

		handleFillRemoveFileModelData : function(targetElement) {
			var modelFileId = $(targetElement).attr('id').replace('remove-model-file-', '');
			$('#remove-model-file-id').val(modelFileId);

			$.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/model-file/count-dependencies/' + modelFileId)
				.done(function (data) {
					if (data && data != null && data != undefined && data.workflowExecutions > 0) {
						$('#deleteModelFileLabel').addClass('hide');
						$('#canNotDeleteModelFileLabel').removeClass('hide');
						$('#delete-model-file-button').prop('disabled', true);
					}
				});
		},

		handleRemoveModelFileSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/model-file/remove-model-file', {modelFileId: $('#remove-model-file-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-model-file-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('modelFileRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover arquivo modelo.');
				});
		},

		handleCompareFilesData : function(targetElement) {
			var modelFileId = $(targetElement).attr('id').replace('compare-files-', '');

			$('.alter-file-model-file-id').val(modelFileId);
			$('.alter-file-workflow-id').val($('#workflow-id').val());

			var currentFileContent = $('#current-model-file-content').val();
			var rowFileContent = $('#model-file-content-' + modelFileId).val();

			// start diff plugin
			var diffMatchPatch = new diff_match_patch();
			var diffNodes = diffMatchPatch.diff_main(currentFileContent, rowFileContent);
			diffMatchPatch.diff_cleanupSemantic(diffNodes);
			var diffHtml = diffMatchPatch.diff_prettyHtml(diffNodes);

			$('.files-comparson-diff').html(diffHtml);
		}
	}

}(jQuery);
