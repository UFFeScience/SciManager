$(function() {

	tagModule.init();

});

var tagModule = function($) {

	return {

		init : function() {
			sciManagerCommons.bindFormValidation($('#create-tag-form'));
			tagModule.bindUIEvents();
			tagModule.renderTagsData();
		},

		renderTagsData : function() {
			var requestData = {
				url: sciManagerCommons.getUrlContext(window.location) + $('#search-url').val(),
				container: '.page-main-content',
				errorMessage: 'Erro ao buscar tags',
				paginationSelector: '.page-link:not(.disabled)',
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
			// submit create-tag form
			$('body').off('click', '#create-tag-submit-button')
					 .on('click', '#create-tag-submit-button', function() {
				tagModule.handleCreateTagSubmit(this);
			});

			// trigger edit tag modal
			$('body').off('click', '.edit-tag').on('click', '.edit-tag', function() {
				tagModule.handleFillEditTagData(this);
			});

			// handle edit tag submit
			$('body').off('click', '#edit-tag-submit-button')
					 .on('click', '#edit-tag-submit-button', function() {
				tagModule.handleEditTagSubmit();
			});

			// trigger remove tag
		    $('body').off('click', '.remove-tag-button')
					 .on('click', '.remove-tag-button', function() {
				tagModule.handleFillRemoveTagData(this);
		    });

		    // submit remove tag
		    $('body').off('click', '#delete-tag-button')
					 .on('click', '#delete-tag-button', function() {
				tagModule.handleRemoveTagSubmit();
		    });

			// handle browser back button
            $(window).off('popstate').on('popstate', function() {
                tagModule.renderTagsData();
            });
		},

		handleCreateTagSubmit : function() {
			if (!validator.checkAll($('#create-tag-form'))) {
				sciManagerCommons.unlockActionButton($('#create-tag-submit-button'));
			}
			else {
				$.post(sciManagerCommons.getUrlContext(window.location) + '/tag/create-tag', $('#create-tag-form').serializeArray())
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('.bs-create-tag-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
							tagModule.renderTagsData();
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao criar tag.');
					});
			}
		},

		handleFillEditTagData : function(targetElement) {
			var tagId = $(targetElement).attr('id').replace('edit-tag-', '');
			var tagName = $('#tag-name-' + tagId).text();

			$('#edit-tag-id').val(tagId);
			$('#edit-tag-name').val(tagName);
		},

		handleFillRemoveTagData : function(targetElement) {
			var tagId = $(targetElement).attr('id').replace('remove-tag-', '');
			$('#remove-tag-id').val(tagId);
		},

		handleRemoveTagSubmit : function() {
			$.post(sciManagerCommons.getUrlContext(window.location) + '/tag/remove-tag', {tagId: $('#remove-tag-id').val()})
				.done(function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-remove-tag-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						tagModule.renderTagsData();
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao remover tag.');
				});
		},

		handleEditTagSubmit : function() {
			if (!sciManagerCommons.isValid($('#edit-tag-name').val())) {
				$('#edit-tag-name').trigger('blur');
				sciManagerCommons.unlockActionButton($('#edit-tag-name-submit-button'));
			}
			else {
				var requestData = {
					tagId: $('#edit-tag-id').val(),
					tagName: $('#edit-tag-name').val()
				};
				$.post(sciManagerCommons.getUrlContext(window.location) + '/tag/edit-tag', requestData)
					.done(function(data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							$('#tag-name-' + requestData.tagId).text(requestData.tagName);
							$('.bs-edit-tag-modal-lg').modal('hide');
							sciManagerCommons.buildSuccessMessage(data.text);
						}
						else {
							sciManagerCommons.buildErrorMessage(data.text);
						}
					})
					.fail(function(data) {
						sciManagerCommons.buildErrorMessage('Erro ao editar nome da tag.');
					});
			}
		}
	}

}(jQuery);
