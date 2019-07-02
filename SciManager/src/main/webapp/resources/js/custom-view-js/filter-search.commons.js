var filterSearchCommons = function($) {

	return {

		pushFiltersToUrl : function(excludedFilters, paramsToKeep) {
			var data = {},
				rawUrl = window.location.toString().split('?')[0];

			filterSearchCommons.gerDataFromSearchInputs(data, excludedFilters);
			filterSearchCommons.gerDataFromExistingFilters(data, excludedFilters);
			filterSearchCommons.addFixedParams(data, paramsToKeep);

			if (!$.isEmptyObject(data)) {
				rawUrl = filterSearchCommons.addParametersToUrl(rawUrl, data);
				window.history.pushState({path: rawUrl}, '', rawUrl);
			}
		},

		addFixedParams : function(data, paramsToKeep) {
			if (paramsToKeep !== undefined) {
				for (var i = 0; i < paramsToKeep.length; i++) {
					var paramValue = sciManagerCommons.getRequestParamValue(window.location, paramsToKeep[i]);
					data[paramsToKeep[i]] = paramValue;
				}
			}
		},

		handleClearSingleFilter : function(e, requestData) {
			var filterElement = $(e.target).parents('.filter-alert-info').find('.filter-field'),
				excludedFilters = [];

			excludedFilters.push(filterElement.attr('data-field-name'));
			sciManagerCommons.loading(requestData.container + ' .loading-data-async');

			filterSearchCommons.filterData(excludedFilters, requestData);
		},

		bindPagination : function(requestData) {
			// pagination fetching
			if (requestData.paginationSelector) {
				$('body').off('click', requestData.paginationSelector)
						 .on('click', requestData.paginationSelector, function() {
					sciManagerCommons.loading();
					filterSearchCommons.handlePaginationFetch(this, requestData);
				});
			}
		},

		handlePaginationFetch : function(paginationTarget, requestData) {
			var baseUrl = requestData.url,
				pageNumber = $(paginationTarget).attr('data-page');

			baseUrl = sciManagerCommons.replaceUrlQueryParam({key: 'page', value: pageNumber}, baseUrl);
			requestData.url = sciManagerCommons.replaceUrlQueryParam({key: 'page', value: pageNumber}, baseUrl)

			filterSearchCommons.loadData(requestData);
			filterSearchCommons.handlePushPaginationState(requestData, pageNumber, requestData.url);
		},

		handlePushPaginationState : function(requestData, pageNumber, currentUrlBase) {
			var url = sciManagerCommons.replaceUrlQueryParam({key: 'page', value: pageNumber}, window.location.href.toString());
			window.history.pushState({path: url}, '', url);
		},

		loadData : function(requestData) {
			if (requestData.beforeAction) {
				requestData.beforeAction();
			}

			$.get(filterSearchCommons.addParametersToUrl(requestData.url, sciManagerCommons.getRequestParams(window.location)))
				.done(function(data, status, xhr) {
					if (xhr.getResponseHeader('requiresRedirect') === 'true') {
						window.location.href = window.location.href;
						return;
					}

					sciManagerCommons.hideTooltips();
					$(requestData.container).html(data);
					sciManagerCommons.initializeICheck();
					popoverCommons.initializePopovers();
					sciManagerCommons.initializeChosen($('.chosen-component-input select'));
					$('body').removeClass('modal-open').removeAttr('style');
					asyncDataCommons.getProfileImages();

					if (requestData.afterAction) {
						requestData.afterAction();
					}

					filterSearchCommons.bindSearchEvents(requestData);
				})
				.fail(function() {
					sciManagerCommons.buildErrorMessage(requestData.errorMessage ? requestData.errorMessage : 'Erro ao buscar dados.');
				});
		},

		bindSearchEvents : function(requestData) {
			var containerSelector = (requestData.container ? requestData.container : '');

			// clear filter fields of search
			$('body').off('click', containerSelector + ' .clear-search-fields')
					 .on('click', containerSelector + ' .clear-search-fields', function() {
				filterSearchCommons.clearSearchFields();
			});

			// handle submit search
			$('body').off('click', containerSelector + ' .search-filter-button')
					 .on('click', containerSelector + ' .search-filter-button', function() {
				filterSearchCommons.filterData(undefined, requestData);
			});

			// handle submit search
			$('body').off('click', containerSelector + ' .search-date-button')
					 .on('click', containerSelector + ' .search-date-button', function() {
				filterSearchCommons.filterData(undefined, requestData);
			});

			// clear single filter
			$('body').off('click', containerSelector + ' .clear-search-button')
					 .on('click', containerSelector + ' .clear-search-button', function(e) {
				filterSearchCommons.handleClearSingleFilter(e, requestData);
			});
		},

		filterData : function(excludedFilters, requestData) {
			var data = {},
				url = requestData.url.split('?')[0];

			if (requestData.beforeAction) {
				requestData.beforeAction();
			}

			filterSearchCommons.gerDataFromSearchInputs(data, excludedFilters, requestData);
			filterSearchCommons.gerDataFromChosenInputs(data, excludedFilters, requestData);
			filterSearchCommons.gerDataFromExistingFilters(data, excludedFilters, requestData);

			requestData.url = filterSearchCommons.addParametersToUrl(url, data);
			filterSearchCommons.bindPagination(requestData);

			$.get(requestData.url)
				.done(function(data) {
					sciManagerCommons.hideTooltips();
					$(requestData.container).html(data);

					sciManagerCommons.initializeICheck();
					sciManagerCommons.initializeChosen($('.chosen-component-input select'));
					asyncDataCommons.getProfileImages();

					if (requestData.noFiltersToUrl !== true) {
						filterSearchCommons.pushFiltersToUrl([], requestData.paramsToKeep);
					}

					if (requestData.afterAction) {
						requestData.afterAction();
					}
				})
				.fail(function(data) {
					sciManagerCommons.buildErrorMessage('Erro ao realizar busca.');
				});
		},

		addParametersToUrl : function(urlBase, parameters) {
			var firstParameter = true,
				existingParams = [];

			if (urlBase && urlBase.indexOf('?') > 0) {
				firstParameter = false;

				var urlBaseParams = urlBase.split('?')[1],
					paramsMap = urlBaseParams.split('&');

				for (var i = 0; i < paramsMap.length; i++) {
					existingParams.push(paramsMap[i].split('=')[0]);
				}
			}

			Object.keys(parameters).forEach(function(key) {
				if (parameters[key] && parameters[key] !== '' && existingParams.indexOf(key) < 0) {
					if (firstParameter) {
						urlBase += '?' + key + '=' + parameters[key];
					}
					else {
						urlBase += '&' + key + '=' + parameters[key];
					}
					firstParameter = false;
				}
			});

			return urlBase;
		},

		gerDataFromSearchInputs : function(data, excludedFilters, requestData) {
			$((requestData && requestData.formContainer ? requestData.formContainer : '') + ' #search-form input:not([type=hidden])').each(function() {
				if ($(this).attr('name') && $(this).attr('name') !== undefined &&
					!filterSearchCommons.isExcludedFilter($(this).attr('data-field-name'), excludedFilters)) {

					if ((($(this).attr('type') === 'radio' || $(this).attr('type') === 'checkbox') && $(this).is(':checked') === true &&
					    $(this).val() === 'true') || $(this).attr('type') === 'text') {

						data[$(this).attr('name')] = $(this).val();
					}
				}
			});
		},

		gerDataFromChosenInputs : function(data, excludedFilters, requestData) {
			$((requestData && requestData.formContainer ? requestData.formContainer : '') + ' #search-form .chosen-component-input select').each(function() {
				if (!filterSearchCommons.isExcludedFilter($(this).attr('name'), excludedFilters)) {
					data[$(this).attr('name')] = $(this).val();
				}
			});
		},

		gerDataFromExistingFilters : function(data, excludedFilters, requestData) {
			$((requestData && requestData.formContainer ? requestData.formContainer : '') + ' .filter-field').each(function() {
				if ($(this).is(':visible') && $(this).attr('data-field-name') &&
					!filterSearchCommons.isExcludedFilter($(this).attr('data-field-name'), excludedFilters)) {

					data[$(this).attr('data-field-name')] = $(this).attr('data-field-value');
				}
			});
		},

		isExcludedFilter : function(fieldName, excludedFilters) {
			if (!excludedFilters || excludedFilters === undefined || excludedFilters === null || excludedFilters.length === 0) {
				return false;
			}

			for (var i = 0; i < excludedFilters.length; i++) {
				if (excludedFilters[i] === fieldName) {
					return true;
				}
			}

			return false;
		},

		clearSearchFields : function() {
			$('#search-form .chosen-component-input select').each(function() {
				sciManagerCommons.deselectChosenOption($(this));
			});

			$('#search-form #currentSearch').val('');
			$('#search-form input[name="initialDate"]').val('');
			$('#search-form input[name="finalDate"]').val('');
		}
	}

}(jQuery);
