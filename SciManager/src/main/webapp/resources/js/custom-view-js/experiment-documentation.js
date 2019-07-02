
var scientificExperimentDocumentationModule = function($) {
	var scientificExperimentHtmlBackup = '';

	return {

		init : function() {
			scientificExperimentDocumentationModule.bindUIEvents();
		},

		renderDocumentationData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-experiment-documentation-tab-url').val(),
				container: '.tab-container .tab-content',
				errorMessage: 'Erro ao buscar documentação de experimento científicos',
				paramsToKeep: ['tab'],
				afterAction: function() {
					sciManagerCommons.handleDocumentationData('scientific-experiment');
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
			$('body').off('click', '#edit-scientific-experiment-html')
					 .on('click', '#edit-scientific-experiment-html', function() {
				scientificExperimentHtmlBackup = sciManagerCommons.handleEditDocumentation('scientific-experiment');
			});

			// trigger cancel edit of html documentation
			$('body').off('click', '#cancel-scientific-experiment-html')
					 .on('click', '#cancel-scientific-experiment-html', function() {
				sciManagerCommons.handleCancelEditDocumentation('scientific-experiment', scientificExperimentHtmlBackup);
				scientificExperimentHtmlBackup = '';
			});

			// trigger save of html documentation
			$('body').off('click', '#save-scientific-experiment-html')
					 .on('click', '#save-scientific-experiment-html', function() {
				scientificExperimentDocumentationModule.handleSubmitHtmlContent();
			});
		},

		handleSubmitHtmlContent : function() {
			var scientificExperimentId = $('#documentation-scientific-experiment-id').val(),
				htmlContent = sciManagerCommons.getHtmlContent('scientific-experiment');

			if (htmlContent == null || htmlContent.trim() == '') {
				sciManagerCommons.buildInfoMessage('Documentação não pode ser vazia.');
				return;
			}

			$('.html-scientific-experiment-doc-content').destroy();
			$('#save-scientific-experiment-html').hide();
			$('#cancel-scientific-experiment-html').hide();

			var data = {
				scientificExperimentId: scientificExperimentId,
				htmlDocumentation: htmlContent
			};

			$.post(sciManagerCommons.getUrlContext(window.location) + '/scientific-experiment/save-experiment-documentation', data)
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						if ($('.no-documentation-yet') && $('.documentation-buttons') != null) {
							$('.no-documentation-yet .close').trigger('click');
						}
						sciManagerCommons.buildSuccessMessage(data.text);
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro inesperado ao salvar documentação do experimento científico.');
				});

			$('#edit-scientific-experiment-html').removeClass('hide');
		}
	}

}(jQuery);
