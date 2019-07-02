$(function() {

	modelFileCommons.init();

});

var modelFileCommons = function($) {
	var viewFileOpened = false;

	return {

		init : function() {
			sciManagerCommons.destroyFile($('#model-file'));
			modelFileCommons.bindUIEvents();
		},

		bindUIEvents : function() {
			// trigger view model file of a workflow
			$('body').off('click', '.view-file').on('click', '.view-file', function(e) {
				modelFileCommons.handleOpenModelFileModal(e, this);
			});

			// only open one modal at a time
			$('body').off('show.bs.modal', '.bs-file-modal-lg')
					 .on('show.bs.modal', '.bs-file-modal-lg', function(e) {

				if (viewFileOpened) {
					return false;
				}
				viewFileOpened = true;
			});

			// close view file modal
			$('body').off('hidden.bs.modal', '.bs-file-modal-lg')
					 .on('hidden.bs.modal', '.bs-file-modal-lg', function(e) {

				viewFileOpened = false;
			});

			// trigger upload of file after file selection
			$('body').off('change', '#model-file').on('change', '#model-file', function() {
				sciManagerCommons.loading();
				modelFileCommons.handleUploadFile();
			});

			// trigger hide loading icon
			$('body').off('shown.bs.modal', '.bs-upload-file-modal-lg')
					 .on('shown.bs.modal', '.bs-upload-file-modal-lg', function(e) {

				if ($(e.target).hasClass('bs-upload-file-modal-lg')) {
					sciManagerCommons.loaded();
					sciManagerCommons.unlockActionButton($('#destroy-model-file'));
				}
			});

			// trigger destroy file
			$('body').off('hidden.bs.modal', '.bs-upload-file-modal-lg')
					 .on('hidden.bs.modal', '.bs-upload-file-modal-lg', function(e) {

				if ($(e.target).hasClass('bs-upload-file-modal-lg')) {
					sciManagerCommons.destroyFile($('#model-file'));
				}
			});

			// trigger save file uploaded
			$('body').off('click', '#upload-new-model-file')
					 .on('click', '#upload-new-model-file', function() {
				modelFileCommons.handleUploadFileSubmit();
			});

			// trigger hide loading icon for upload file modal
			$('body').off('shown.bs.modal', '.bs-upload-file-modal-lg')
					 .on('shown.bs.modal', '.bs-upload-file-modal-lg', function(e) {

				if ($(e.target).hasClass('bs-upload-file-modal-lg')) {
					sciManagerCommons.loaded();
				}
			});
		},

		handleUploadFileSubmit : function() {
	        $.ajax({
	            type: 'POST',
	            enctype: 'multipart/form-data',
	            url: sciManagerCommons.getUrlContext(window.location) + '/workflow/model-file/upload-model-file',
	            data: new FormData($('#upload-new-model-file-form')[0]),
	            processData: false,
	            contentType: false,
	            cache: false,
	            success: function(data) {
					if (sciManagerCommons.isSuccessType(data.type)) {
						$('.bs-upload-file-modal-lg').modal('hide');
						sciManagerCommons.buildSuccessMessage(data.text);
						sciManagerCommons.publishEvent('modelFileRefresh', {'detail': {'reload': true}});
					}
					else {
						sciManagerCommons.buildErrorMessage(data.text);
					}

					sciManagerCommons.unlockActionButton($('#upload-new-model-file'));
	            },
	            error: function (e) {
					sciManagerCommons.buildErrorMessage('Erro ao realizar upload de novo arquivo modelo.');
					sciManagerCommons.unlockActionButton($('#upload-new-model-file'));
	            }
	        });
		},

		handleOpenModelFileModal : function(event, targetElement) {
			if ($(targetElement).attr('id') !== undefined) {
				var modelFileId = $(targetElement).attr('id').replace('view-file-', '');

				$('.file-uploader-name').text($('#file-uploader-name-' + modelFileId).text());
				$('.file-container').text($('#model-file-content-' + modelFileId).val());
			}

			$('.bs-file-modal-lg').modal();
		},

		loadModelFile : function(currentModelFile) {
			if (currentModelFile) {
				$('.file-uploader-name').text(currentModelFile.uploader);
				$('.file-container').text(currentModelFile.fileContent);
				$('.model-file-data').removeClass('hide');
				$('#has-model-file').val('true');
			}
			else{
				$('#has-model-file').val('false');
			}

			$('.loading-model-file').addClass('hide');
		},

		handleUploadFile : function() {
			console.log('?')
			$('#model-file').prop('disabled', true);
			var file = $('#model-file').prop('files')[0];

			if (modelFileCommons.validateFileUploaded(file)) {
				if ($('#has-model-file').val() === 'true') {
					$('.has-model-file').removeClass('hide');
					$('.has-not-model-file').addClass('hide');
				}
				else {
					$('.has-model-file').addClass('hide');
					$('.has-not-model-file').removeClass('hide');
				}
				$('.bs-upload-file-modal-lg').modal('show');

				var reader = new FileReader();
				reader.onload = function(e) {
					// start diff plugin
					var currentFileContent = $('#current-model-file-content').val();
					var newFileContent = reader.result;

					if (!currentFileContent || currentFileContent == null) {
						currentFileContent = newFileContent;
					}

					var diffMatchPatch = new diff_match_patch();
					var diffNodes = diffMatchPatch.diff_main(currentFileContent, newFileContent);
					diffMatchPatch.diff_cleanupSemantic(diffNodes);
					var diffHtml = diffMatchPatch.diff_prettyHtml(diffNodes);

					$('.files-diff').html(diffHtml);
					$('#model-file').prop('disabled', false);
				}
				reader.readAsText(file);
			}
		},

		validateFileUploaded : function(file) {
			if (!sciManagerCommons.isValid(file) || !sciManagerCommons.isValid($('#model-file').val())) {
				sciManagerCommons.destroyFile($('#model-file'));
				$('.upload-spin-loader').addClass('upload-spin-loader-hidden');
				$('#model-file').prop('disabled', false);
				return false;
			}

			if (file.size > 524288) {
				sciManagerCommons.destroyFile($('#model-file'));
				sciManagerCommons.buildWarningMessage('Arquivo excede o limite de 500 kb.');
				$('.upload-spin-loader').addClass('upload-spin-loader-hidden');
				$('#model-file').prop('disabled', false);
				return false;
			}

			var fileName = $('#model-file').val().split('.');
			if (fileName.length < 2) {
				sciManagerCommons.destroyFile($('#model-file'));
				sciManagerCommons.buildErrorMessage('Erro inesperado ao processar arquivo modelo.');
				$('.upload-spin-loader').addClass('upload-spin-loader-hidden');
				$('#model-file').prop('disabled', false);
				return false;
			}

			var fileExtension = fileName[1].toLowerCase();
			if (fileExtension != 'xml') {
				sciManagerCommons.destroyFile($('#model-file'));
				sciManagerCommons.buildErrorMessage('Formato de arquivo modelo inválido, apenas arquivos ".xml" são aceitos.');
				sciManagerCommons.destroyFile($('#model-file'));
				$('.upload-spin-loader').addClass('upload-spin-loader-hidden');
				$('#model-file').prop('disabled', false);
				return false;
			}

			return true;
		}
	}

}(jQuery);
