$(function() {

	sciManagerCommons.init();

});

var sciManagerCommons = function($) {

	return {

		init : function() {
			sciManagerCommons.initializeICheck();
			sciManagerCommons.initializeSecurityAction();
			sciManagerCommons.addJqueryIncludeCachedScript();
			sciManagerCommons.initializePolyfillEventHandler();
			sciManagerCommons.initializeAsyncContent();
			sciManagerCommons.initializeValidator();
			sciManagerCommons.initializeTooltips();
			sciManagerCommons.initializeDropdownsBehavior();
		},

		initializeDropdownsBehavior : function() {
			// make dropdown not close when clicked unless it is a button or a link
			$('body').off('click', '.dropdown-menu').on('click', '.dropdown-menu', function(e) {
			    if (e.target.nodeName !== 'BUTTON' || e.target.nodeName !== 'A') {
					e.stopPropagation();
				}
			});

			var dropdownMenu;
			var detached = false;

			// fix dropdown beahavior in tables when other dropdown is clicked
		    $(window).off('show.bs.dropdown').on('show.bs.dropdown', function (e) {
		        dropdownMenu = $(e.target).find('.dropdown-menu');

		        if (!dropdownMenu.parents('.table-container').length) {
		        	detached = false;
		        	return;
		        }
		        $('body').append(dropdownMenu.detach());
		        detached = true;
		        var eOffset = $(e.target).offset();

		        dropdownMenu.css({
		            'display': 'block',
		            'top': eOffset.top + $(e.target).outerHeight(),
		            'right': window.innerWidth - eOffset.left - $(e.target).outerWidth()
		        });
		    });

		    $(window).off('hide.bs.dropdown').on('hide.bs.dropdown', function (e) {
		    	if (!detached) {
		        	return;
		        }
		        $(e.target).append(dropdownMenu.detach());
		        dropdownMenu.hide();
		    });
		},

		initializeSecurityAction : function() {
			// add security token to header of all ajax requisitions and accept-charset="UTF-8" to all forms
			$(function() {
				$('form').attr('accept-charset', 'UTF-8');
				$('input').attr('autocomplete', 'off');

			    var token = $('meta[name="_csrf"]').attr('content');
			    var header = $('meta[name="_csrf_header"]').attr('content');

				if (token && header) {
					$(document).ajaxSend(function(e, xhr, options) {
						xhr.setRequestHeader(header, token);
					});
				}
			});
		},

		initializeTooltips : function() {
			// fix tooltip in appended elements
			$('body').tooltip({
			    selector: '[data-toggle=tooltip]',
				container: 'body'
			});
		},

		hideTooltips : function() {
			$('.tooltip').tooltip('hide');
		},

		clearDateComponents : function() {
			$('.daterangepicker').remove();
		},

		initializeDateComponents : function(selector) {
			// start calendar

			var optionDateSet = {
				autoUpdateInput: false,
		        timePicker: false,
		        showDropdowns: true,
		        timePickerIncrement: 1,
		        timePicker12Hour: true,
		        format: 'DD/MM/YYYY',
		        singleDatePicker: true,
		        calender_style: "picker_2",
		        locale: {
		            daysOfWeek: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'],
		            monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro']
		        }
		    };

			if (selector !== undefined) {
				$(selector).daterangepicker(optionDateSet);
			}
			else {
				$('.calendar-filter').daterangepicker(optionDateSet);
			}

			// special mask for dates
			$('.date-input').mask('99/99/9999');
		},

		initializeDateOptionsComponents : function(selector, selectFirst) {
			// start calendar

			var optionDateSet = {
		        format: 'DD/MM/YYYY',
		        calender_style: 'picker_2',
		        applyClass: 'hide',
		        cancelClass: 'hide',
		        locale: {
		        	applyLabel: 'Confirmar',
		            cancelLabel: 'Cancelar',
		            fromLabel: 'De',
		            toLabel: 'Até',
		            daysOfWeek: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'],
		            monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro']
		        },
				ranges: {
			        'Últimos 7 dias': [moment().subtract(6, 'days'), moment()],
			        'Últimos 14 dias': [moment().subtract(13, 'days'), moment()],
			        'Últimos 30 dias': [moment().subtract(29, 'days'), moment()],
			        'Últimos 90 dias': [moment().subtract(89, 'days'), moment()]
			    }
		    };

			if (selector !== undefined) {
				$(selector).daterangepicker(optionDateSet);
			}
			else {
				$('.calendar-filter').daterangepicker(optionDateSet);
			}

			// hide options
			$('.daterangepicker .ranges li:first').addClass('active');
			$('.calendar.first.right').hide();
			$('.calendar.second.left').hide();
			$('.daterangepicker .ranges li:last').hide();
			$('.daterangepicker_start_input').hide();
			$('.daterangepicker_end_input').hide();

			// special mask for dates
			$('.date-input').mask('99/99/9999');

			// Remove gap of datepicker buttons removed
			$('.range_inputs').addClass('hide');

			if (selectFirst === true) {
				$('.ranges ul li:first').click();
			}
		},

		addJqueryIncludeCachedScript : function() {
			// include cached scripts
			jQuery.include = function(url, options) {

				options = $.extend( options || {}, {
					dataType: 'script',
					cache: true,
					url: url
				});

				return jQuery.ajax(options);
			};
		},

		initializeAsyncContent : function() {
			// show loaded content
			$(window).load(function () {
				$('.loading-content-spin').addClass('hide');
				$('.loaded-content').removeClass('hide');
			});

			// hide action button and show loading button
			$('body').off('click', '.action-button').on('click', '.action-button', function() {
				sciManagerCommons.lockActionButton(this);
			});

			// show action buttons back on modal close
			$('.modal').off('hidden.bs.modal').on('hidden.bs.modal', function() {
				var actionButton = $('.action-button');

				if (actionButton && actionButton !== null && actionButton !== undefined) {
					$(actionButton).removeClass('disabled');
					$(actionButton).find('.action-text').removeClass('hide');
					$(actionButton).find('.loading-action').addClass('hide');
				}
			});
		},

		initializeICheck : function() {
			if ($("input.flat")[0]) {
		        $('input.flat').iCheck({
		            checkboxClass: 'icheckbox_flat-green',
		            radioClass: 'iradio_flat-green'
		        });
			}
		},

		initializeValidator : function() {
			// initialize the validator function
			// validate a field on "blur" event, a 'select' on 'change' event & a '.reuired' classed multifield on 'keyup':
			$('form').off('blur', 'input[required], input.optional, select.required, .chosen-component-input')
					 .off('change', 'select.required')
					 .off('keypress', 'input[required][pattern]')
					 .on('blur', 'input[required], input.optional, select.required, .chosen-component-input', validator.checkField)
					 .on('change', 'select.required', validator.checkField)
					 .on('keypress', 'input[required][pattern]', validator.keypress);

			$('.chosen-component-input li.active-result').off('keypress').on('keypress', function() {
				sciManagerCommons.triggerFieldBlur($('.chosen-component-input'));
			});

			$('.multi.required').off('keyup blur', 'input').on('keyup blur', 'input', function () {
				validator.checkField.apply($(this).siblings().last()[0]);
			});
		},

		lockActionButton : function(actionButton) {
			$(actionButton).addClass('disabled');
			$(actionButton).find('.action-text').addClass('hide');
			$(actionButton).find('.loading-action').removeClass('hide');
		},

		unlockActionButton : function(actionButton) {
			$(actionButton).removeClass('disabled');
			$(actionButton).find('.action-text').removeClass('hide');
			$(actionButton).find('.loading-action').addClass('hide');
		},

		bindFormValidation : function(formSelector) {
			// bind the validation to the form submit event
			$(formSelector).submit(function(e) {
			    e.preventDefault();
			    var submit = true;
			    // evaluate the form using generic validaing
			    if (!validator.checkAll($(this))) {
			        submit = false;
			    }

			    if (submit) {
			        this.submit();
			    }
			    else {
			    	sciManagerCommons.unlockActionButton($('.action-button'));
			    }

		        return false;
			});
		},

		triggerSuccessAlert : function(element, message) {
			// trigger success message alert
			if ($(element).hasClass('hasSuccessMessage')) {
	            sciManagerCommons.buildSuccessMessage(message);
			}
		},

		triggerErrorAlert : function(element, message) {
			// trigger error message alert
			if ($(element).hasClass('hasErrorMessage')) {
	            sciManagerCommons.buildErrorMessage(message);
			}
		},

		triggerWarningAlert : function(element, message) {
			// trigger warning message alert
			if ($(element).hasClass('hasWarningMessage')) {
	            sciManagerCommons.buildWarningMessage(message);
			}
		},

		triggerInfoAlert : function(element, message) {
			// trigger info message alert
			if ($(element).hasClass('hasInfoMessage')) {
	            sciManagerCommons.buildInfoMessage(message);
			}
		},

		buildWarningMessage : function(message, hide) {
			if (hide === false) {
				new PNotify({
	                title: 'Atenção',
	                text: message,
					hide: false,
	                animate_speed: "fast"
	            });
			}
			else {
				new PNotify({
	                title: 'Atenção',
	                text: message,
	                animate_speed: "fast"
	            });
			}
		},

		buildInfoMessage : function(message) {
			new PNotify({
                title: 'Informação',
                text: message,
                type: 'info',
                hide: false,
                animate_speed: "fast"
            });
		},

		buildSuccessMessage : function(message) {
			new PNotify({
                title: 'Sucesso',
                text: message,
                animate_speed: "fast",
                type: 'success'
            });
		},

		buildErrorMessage : function(message) {
			if (!message) {
				message = 'Erro inesperado ao realizar ação.'
			}
			new PNotify({
				title: 'Erro',
		        text: message,
		        type: 'error',
		        animate_speed: "fast",
                hide: false
            });
		},

		isErrorType : function(messageType) {
			return messageType == 'errorMessage'
		},

		isSuccessType : function(messageType) {
			return messageType == 'successMessage'
		},

		initializeChosen : function(target) {
			// initialize chosen
			if ($.isArray(target) || $(target).length > 1) {
				for (var i = 0; i < target.length; i++) {
					sciManagerCommons.initializeSingleChosen(target[i]);
				}
			}
			else {
				sciManagerCommons.initializeSingleChosen(target);
			}
		},

		initializeSingleChosen : function(chosenSelector) {
			if (($(chosenSelector).find('.chosen-container') === undefined || $(chosenSelector).find('.chosen-container').length === 0 ||
				!$(chosenSelector).find('.chosen-container')) && ($(chosenSelector) !== undefined &&
				$(chosenSelector).chosen !== undefined && typeof $(chosenSelector).chosen === 'function')) {

				$(chosenSelector).chosen({
					placeholder_text_multiple: 'Buscar...',
					placeholder_text_single: 'Buscar...',
					no_results_text: 'Buscando por:'
				});
				sciManagerCommons.startChosenFilter(chosenSelector);
			}
		},

		startChosenFilter : function(chosenElement) {
			if (!chosenElement) {
				return;
			}

			var attributeName = $(chosenElement).attr('name');

			if (attributeName && $('.filter-field[data-field-name="' + attributeName + '"]')) {
				var filterValue = $('.filter-field[data-field-name="' + attributeName + '"]').text();

				$(chosenElement).append('<option value="' + filterValue + '">' + filterValue + '</option>');
				$(chosenElement).val(filterValue);
				$(chosenElement).trigger('chosen:updated');
			}
		},

		handleChosenSearch : function(chosenElement, url, firstFieldName, secondFieldName) {
			$.get(url)
				.done(function (data) {
					$(chosenElement + ' option:not(:selected)').remove();

					if (data.length == 0) {
						$(chosenElement).append('<option disabled="disabled" value="">Nenhum registro encontrado.</option>');
					}
					else {
						$.each(data, function(index) {
							if ($(chosenElement + ' option[value="' + data[index][firstFieldName] + '"]').length == 0) {
								if (secondFieldName) {
									$(chosenElement).append('<option value="' + data[index][firstFieldName] + '">' + data[index][secondFieldName] + '</option>');
								}
								else {
									$(chosenElement).append('<option value="' + data[index][firstFieldName] + '">' + data[index][firstFieldName] + '</option>');
								}
							}
						});
					}

					$(chosenElement).trigger('chosen:updated');
				})
				.fail(function() {
					$(chosenElement).empty().append('<option disabled="disabled" value="">Erro ao fazer a busca.</option>');
					$(chosenElement).trigger('chosen:updated');
				});
		},

		clearChosenSelects : function(selector) {
			$(selector).empty().append('<option value=""></option>');
			$(selector).trigger('chosen:updated');
		},

		deselectChosenOption : function(selector) {
			$(selector).val('');
			$(selector).trigger('chosen:updated');
		},

		isValid : function(value) {
			return value && value !== null && value !== '' && value !== undefined;
		},

		getRequestParams : function(url) {
			var params = {},
				urlSearch = '?';

			if (url.search !== undefined) {
				urlSearch = url.search;
			}
			else if (url.indexOf('?') < 0) {
				return [];
			}
			else {
				urlSearch = url.split('?')[1];
			}

			urlSearch.substr(1).split('&').forEach(function(item) {
			    var splittedParams = item.split('='),
			        key = splittedParams[0],
			        value = splittedParams[1] && decodeURIComponent(splittedParams[1]);
			    (params[key] = params[key] || []).push(value);
			});

			return params;
		},

		getRequestParamValue : function(url, paramKey) {
			var params = sciManagerCommons.getRequestParams(url);
			return params[paramKey] && params[paramKey].length === 1 ? params[paramKey][0] : params[paramKey];
		},

		getUrlContext : function(location) {
		    var context = $('#application-context-path').val();
		    return location.protocol + '//' + location.host + (context != undefined ? context : '');
		},

		getModuleUrl : function(fullUrl) {
			if (!fullUrl || fullUrl == null) {
				return null;
			}

			var applicationUrl = sciManagerCommons.getUrlContext(window.location);
			var partialUrl = fullUrl.replace(applicationUrl + '/', '');
			var moduleUrl = '';

		    for (var i = 0; i < partialUrl.length; i++) {
		    	if (partialUrl.charAt(i) != '?' && partialUrl.charAt(i) != '/') {
		    		moduleUrl += partialUrl.charAt(i);
		    	}
		    	else {
		    		break;
		    	}
		    }

		    return applicationUrl + '/' + moduleUrl;
		},

		isIEBrowser : function() {
			var ua = window.navigator.userAgent;
		    var msie = ua.indexOf("MSIE ");

		    if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./)) {
		        return true;
		    }

		    return false;
		},

		typewatch : function() {
			var timer = 0;

			return function(callback, ms) {
				clearTimeout(timer);
				timer = setTimeout(callback, ms);
			};
		}(),

		triggerFieldBlur : function(element) {
			$(element).trigger('blur')
		},

		loading : function(selector) {
			if (selector !== undefined) {
				$(selector).removeClass('hide');
			}
			else {
				$('.loading-data-async').removeClass('hide');
			}
		},

		loaded : function(selector) {
			if (selector !== undefined) {
				$(selector).addClass('hide');
			}
			else {
				$('.loading-data-async').addClass('hide');
			}
		},

		destroyFile : function(element) {
			// function that clears file input
			if (element && element.length > 0 && element !== null && element !== undefined) {
				$(element).wrap('<form>').closest('form').get(0).reset();
				$(element).unwrap();
			}
		},

		getHtmlContent : function(entitySelector) {
			var htmlContent = $('.html-' + entitySelector + '-doc-content').code();
			var htmlText = '';

			try {
				htmlText = $(htmlContent).text();
			}
			catch(err) {
				htmlText = htmlContent;
			}

			return htmlText;
		},

		handleDocumentationData : function(entitySelector) {
			var htmlParsed = $('.html-' + entitySelector + '-doc-content .html-content').text();
			$('.html-' + entitySelector + '-doc-content').html('');
			$('.html-' + entitySelector + '-doc-content').html(htmlParsed);
		},

		handleCancelEditDocumentation : function(entitySelector, htmlBackup) {
			$('#cancel-' + entitySelector + '-html').addClass('hide');
			$('#save-' + entitySelector + '-html').addClass('hide');
			$('#edit-' + entitySelector + '-html').removeClass('hide');

			$('.html-' + entitySelector + '-doc-content').code(htmlBackup);
			$('.html-' + entitySelector + '-doc-content').destroy();
		},

		handleEditDocumentation : function(entitySelector) {
			$('#save-' + entitySelector + '-html').removeClass('hide');
			$('#edit-' + entitySelector + '-html').addClass('hide');
			$('#cancel-' + entitySelector + '-html').removeClass('hide');

			$('.html-' + entitySelector +'-doc-content').summernote({dialogsInBody: true, focus: true});

			// hide image upload
			$('.note-image-dialog .note-image-input, .note-image-dialog h5:first').addClass('hide');

			return $('.html-' + entitySelector + '-doc-content').code();
		},

		initializeTagIt : function(selector) {
			if ($(selector) && $(selector) !== undefined && $(selector).length) {
				$(selector).tagit({
					placeholderText: 'Adicionar tag',
					fieldName: 'tags',
						autocomplete: {
							source: function(request, response) {
								$.ajax({
									url: sciManagerCommons.getUrlContext(window.location) + '/tag/api/all-tags',
									dataType: 'json',
									type: 'GET',
									data: {
										search: request.term
									},
									success: function(data) {
										response($.map(data, function(tag) {
				                            return {
				                                label: tag,
				                                value: tag
				                            }
				                        }));
									}
								});
						},
						minLength: 2
					}
				});
			}
		},

		replaceUrlQueryParam : function(targetParam, targetUrl) {
			var url = targetUrl === undefined ? window.location.toString() : targetUrl.toString(),
				baseUrl = url.split('?')[0];

			if (url.indexOf('?') > 0) {
				var paramsResult = '?',
				 	urlParams = url.split('?')[1],
					paramsList = urlParams.split('&'),
					hasParameter = false;

				for (var i = 0; i < paramsList.length; i++) {
					paramData = paramsList[i].split('=');
					if (paramData.length > 1 && paramData[0] === targetParam.key) {
						paramsList[i] = targetParam.key + '=' + targetParam.value;
						hasParameter = true;
					}
				}

				if (!hasParameter) {
					paramsList.push(targetParam.key + '=' + targetParam.value);
				}

				for (var j = 0; j < paramsList.length; j++) {
					paramsResult += paramsList[j];
					if ((j + 1) < paramsList.length) {
						paramsResult += '&';
					}
				}
				url = baseUrl + paramsResult;
			}
			else {
				url = baseUrl + '?' + targetParam.key + '=' + targetParam.value;
			}

			return url;
		},

		urlContainsSameQueryParam : function(targetParam, targetUrl) {
			var url = targetUrl === undefined ? window.location.toString() : targetUrl.toString();

			if (url.indexOf('?') > 0) {
				var urlParams = url.split('?')[1],
					paramsList = urlParams.split('&'),
					hasParameter = false;

				for (var i = 0; i < paramsList.length; i++) {
					paramData = paramsList[i].split('=');
					if (paramData.length > 1 && paramData[0] === targetParam.key && paramData[1] === targetParam.value) {
						hasParameter = true;
						break;
					}
				}
				return hasParameter;
			}
			return false;
		},

		updateTabUrl : function(currentTab) {
            if (currentTab !== undefined) {
				var tabUrl = '',
					tabParam = {key: 'tab', value: currentTab};

				if (sciManagerCommons.urlContainsSameQueryParam(tabParam, window.location.href)) {
					tabUrl = sciManagerCommons.replaceUrlQueryParam(tabParam, window.location.href);
				}
				else {
					tabUrl = window.location.href.split('?')[0] + '?' + tabParam.key + '=' + tabParam.value;
				}

				if (window.location.href !== tabUrl) {
					window.history.pushState({path: tabUrl}, '', tabUrl);
				}
            }
        },

		publishEvent : function(eventName, payload) {
			document.dispatchEvent(new CustomEvent(eventName, payload));
		},

		initializePolyfillEventHandler : function() {
			!window.addEventListener && (function (WindowPrototype, DocumentPrototype, ElementPrototype, addEventListener, removeEventListener, dispatchEvent, registry) {
				WindowPrototype[addEventListener] = DocumentPrototype[addEventListener] = ElementPrototype[addEventListener] = function (type, listener) {
					var target = this;
					registry.unshift([target, type, listener, function (event) {
						event.currentTarget = target;
						event.preventDefault = function () { event.returnValue = false };
						event.stopPropagation = function () { event.cancelBubble = true };
						event.target = event.srcElement || target;

						listener.call(target, event);
					}]);
					this.attachEvent("on" + type, registry[0][3]);
				};

				WindowPrototype[removeEventListener] = DocumentPrototype[removeEventListener] = ElementPrototype[removeEventListener] = function (type, listener) {
					for (var index = 0, register; register = registry[index]; ++index) {
						if (register[0] == this && register[1] == type && register[2] == listener) {
							return this.detachEvent("on" + type, registry.splice(index, 1)[0][3]);
						}
					}
				};

				WindowPrototype[dispatchEvent] = DocumentPrototype[dispatchEvent] = ElementPrototype[dispatchEvent] = function (eventObject) {
					return this.fireEvent("on" + eventObject.type, eventObject);
				};
			})(Window.prototype, HTMLDocument.prototype, Element.prototype, "addEventListener", "removeEventListener", "dispatchEvent", []);
		}
	}

}(jQuery);
