$(function() {

	workflowDocumentationModule.init();

});

var workflowDocumentationModule = function($) {
	var workflowHtmlBackup = '';

	return {

		init : function() {
			workflowDocumentationModule.bindUIEvents();
		},

		renderDocumentationData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-workflow-documentation-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar documentação de workflow',
				paramsToKeep: ['tab'],
				afterAction: function() {
					sciManagerCommons.handleDocumentationData('workflow');
					sciManagerCommons.loaded();
				},
				beforeAction: function() {
					sciManagerCommons.loading();
				}
			};

			filterSearchCommons.loadData(requestData);
		},

		bindUIEvents : function() {
			// trigger edit of html documentation
			$('body').off('click', '#edit-workflow-html')
					 .on('click', '#edit-workflow-html', function() {
				workflowHtmlBackup = sciManagerCommons.handleEditDocumentation('workflow');
			});

			// trigger cancel edit of html documentation
			$('body').off('click', '#cancel-workflow-html')
					 .on('click', '#cancel-workflow-html', function() {
				sciManagerCommons.handleCancelEditDocumentation('workflow', workflowHtmlBackup);
				workflowHtmlBackup = '';
			});

			// trigger save of html documentation
			$('body').off('click', '#save-workflow-html')
					 .on('click', '#save-workflow-html', function() {
				workflowDocumentationModule.handleSubmitHtmlContent();
			});
		},

		handleSubmitHtmlContent : function() {
			var workflowId = $('#documentation-workflow-id').val(),
				htmlContent = sciManagerCommons.getHtmlContent('workflow');

			if (htmlContent == null || htmlContent.trim() == '') {
				sciManagerCommons.buildInfoMessage('Documentação não pode ser vazia.');
				return;
			}

			$('.html-workflow-doc-content').destroy();
			$('#save-workflow-html').hide();
			$('#cancel-workflow-html').hide();

			var data = {
				workflowId: workflowId,
				htmlDocumentation: htmlContent
			};

			$.post(sciManagerCommons.getUrlContext(window.location) + '/workflow/save-workflow-documentation', data)
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						if ($('.no-documentation-yet') && $('.documentation-buttons') !== null) {
							$('.no-documentation-yet .close').trigger('click');
						}
						sciManagerCommons.buildSuccessMessage(data.text);
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro inesperado ao salvar documentação do workflow.');
				});

			$('#edit-workflow-html').removeClass('hide');
		}
	}

}(jQuery);
