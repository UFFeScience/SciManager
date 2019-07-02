$(function() {

	scientificProjectDocumentationModule.init();

});

var scientificProjectDocumentationModule = function($) {
	var scientificProjectHtmlBackup = '';

	return {

		init : function() {
			scientificProjectDocumentationModule.bindUIEvents();
		},

		renderDocumentationData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-project-documentation-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar documentação de projeto científicos',
				paramsToKeep: ['tab'],
				afterAction: function() {
					sciManagerCommons.handleDocumentationData('scientific-project');
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
			$('body').off('click', '#edit-scientific-project-html')
					 .on('click', '#edit-scientific-project-html', function() {
				scientificProjectHtmlBackup = sciManagerCommons.handleEditDocumentation('scientific-project');
			});

			// trigger cancel edit of html documentation
			$('body').off('click', '#cancel-scientific-project-html')
					 .on('click', '#cancel-scientific-project-html', function() {
				sciManagerCommons.handleCancelEditDocumentation('scientific-project', scientificProjectHtmlBackup);
				scientificProjectHtmlBackup = '';
			});

			// trigger save of html documentation
			$('body').off('click', '#save-scientific-project-html')
					 .on('click', '#save-scientific-project-html', function() {
				scientificProjectDocumentationModule.handleSubmitHtmlContent();
			});
		},

		handleSubmitHtmlContent : function() {
			var scientificProjectId = $('#documentation-scientific-project-id').val(),
				htmlContent = sciManagerCommons.getHtmlContent('scientific-project');

			if (htmlContent == null || htmlContent.trim() == '') {
				sciManagerCommons.buildInfoMessage('Documentação não pode ser vazia.');
				return;
			}

			$('.html-scientific-project-doc-content').destroy();
			$('#save-scientific-project-html').hide();
			$('#cancel-scientific-project-html').hide();

			var data = {
				scientificProjectId: scientificProjectId,
				htmlDocumentation: htmlContent
			};

			$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-project/save-project-documentation', data)
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
					sciManagerCommons.buildErrorMessage('Erro inesperado ao salvar documentação do projeto científico.');
				});

			$('#edit-scientific-project-html').removeClass('hide');
		}
	}

}(jQuery);
