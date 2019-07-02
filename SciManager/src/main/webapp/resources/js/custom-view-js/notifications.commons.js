$(function() {

	notificationsCommons.init();

});

var notificationsCommons = function($) {

	return {

		init : function() {
			notificationsCommons.getTotalExecutions();
			notificationsCommons.getNotificationsCount();
			notificationsCommons.getNotificationsData();
			notificationsCommons.addEventSourceListener();
			notificationsCommons.bindUIEvents();
		},

		bindUIEvents : function() {
			// mark notification as visualized
		    $('body').off('click', '.notification-item, .visualize-all')
					 .on('click', '.notification-item, .visualize-all', function(e) {
		     	e.preventDefault();
				notificationsCommons.markNotificationVisualized(e);
		    });
		},

		// if not IE, add listener to notifications
		addEventSourceListener : function() {
			if (!sciManagerCommons.isIEBrowser()) {
				var userId = $('#current-user-id').val();
				var notifierUrl = $('#notifer-url').val();

				if (userId && notifierUrl && typeof(EventSource) !== 'undefined' && !!window.EventSource) {
					var notificationMessagesSource = new EventSource(notifierUrl + '/message/sse/' + userId, {withCredentials: true});
					var executionsSource = new EventSource(notifierUrl + '/execution/sse/' + userId, {withCredentials: true});

					notificationMessagesSource.onmessage = function(event) {
						if (event && event.data) {
							var jsonData = JSON.parse(event.data);

							if (jsonData.userId === userId && jsonData.channel === 'message' && jsonData.reload === true) {
								notificationsCommons.getNotificationsCount();
								notificationsCommons.getNotificationsData();
							}
						}
		            }
					executionsSource.onmessage = function(event) {
						if (event && event.data) {
							var jsonData = JSON.parse(event.data);

							if (jsonData.userId === userId && jsonData.channel === 'execution' && jsonData.reload === true) {
								notificationsCommons.getTotalExecutions();
							}
						}
		            }
				}
			}
		},

		// verify if the number of workflow executions has changed
		getTotalExecutions : function() {
			$.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/count-executions')
				.done(function(data) {
					$('#total-executions').text(data);

					if (+data > 0) {
						$('#user-execution-details').removeClass('hide');
						$('#total-executions').removeClass('hide');

						if (+data == 1) {
							$('#user-execution-text').text('Você tem ' + data + ' execução em andamento');
						}
						else {
							$('#user-execution-text').text('Você tem ' + data + ' execuções em andamento');
						}
					}
					else if (+data <= 0) {
						$('#total-executions').addClass('hide');
						$('#user-execution-details').addClass('hide');
						$('#user-execution-text').text('Você tem ' + data + ' execuções em andamento');
					}
					// if IE start polling
					if (sciManagerCommons.isIEBrowser()) {
						setTimeout(notificationsCommons.getTotalExecutions, 10000);
					}
				})
				.fail(function () {}
				);
		},

		// verify if the number of notifications
		getNotificationsCount : function() {
			var userId = $('#current-user-id').val();

			if (userId) {
				$.get(sciManagerCommons.getUrlContext(window.location) + '/notification-message/count/' + userId)
					.done(function(data) {
						if (data) {
							$('#total-message').text(data);

							if (+data > 0) {
								$('#total-message').removeClass('hide');
								if ($('#btn-visualize-all')) {
									$('#btn-visualize-all').removeClass('disabled');
								}
								if (+data > 10) {
									$('#user-notification-details').removeClass('hide');
								}
								else {
									$('#user-notification-details').addClass('hide');
								}
							}
							else if (+data <= 0) {
								$('#total-message').addClass('hide');

								if ($('#btn-visualize-all')) {
									$('#btn-visualize-all').addClass('disabled');
								}
							}
						}
						else {
							$('#total-message').text(0);
							$('#total-message').addClass('hide');

							if ($('#btn-visualize-all')) {
								$('#btn-visualize-all').addClass('disabled');
							}
						}
						// if IE start polling
						if (sciManagerCommons.isIEBrowser()) {
							setTimeout(notificationsCommons.getNotificationsCount, 10000);
						}
					})
					.fail(function () {}
					);
			}
		},

		// get notifications data
		getNotificationsData : function() {
			var userId = $('#current-user-id').val();

			if (userId) {
				$.get(sciManagerCommons.getUrlContext(window.location) + '/notification-message/all/' + userId)
					.done(function(data) {
						var notificationsList = '';

						if (data.length > 0) {
							for (var i = 0; i < data.length; i++) {
								notificationsList += '<li' + (data[i].visualized == false ? ' class="notification-not-visualized"' : '') + '>' +
													 '<a data-message-id="' + data[i].notificationMessageId +
													 '" href="' + sciManagerCommons.getUrlContext(window.location) + data[i].messageLink + '" class="notification-item' + (data[i].visualized == true ? ' notification-visuazed' : '') + '">' +
													 '<span class="message avoid-event">' + data[i].messageBody + '</span>' +
													 '<span class="avoid-event message-action-date"><i class="fa fa-clock-o message-action-date"></i>' + data[i].actionDate + '</span>' +
													 '</a>' +
													 '</li>';
							}

							var totalNotifications = +$('#total-message').text();

							notificationsList += '<li' + (totalNotifications == 0 ? ' class="hide visualize-all-li"' : ' class="visualize-all-li"') + '>' +
												 '<div class="text-center">' +
												 '<a class="visualize-all' + (totalNotifications <= 0 ? ' notification-visuazed' : '') +'">' +
												 '<strong class="avoid-event">Marcar todas como visualizadas</strong> <i class="fa avoid-event pull-right fa-envelope-open"></i>' +
											     '</a>' +
											     '</div>' +
											     '</li>'+
											     '<li>' +
												 '<div class="text-center">' +
												 '<a href="' + sciManagerCommons.getUrlContext(window.location) + '/notification-message/all-notifications/' + userId + '">' +
												 '<strong>Ver todas as notificações</strong> <i class="fa pull-right fa-envelope"></i>' +
												 '</a>' +
												 '</div>' +
												 '</li>';
						}
						else if (!data || data.length <= 0) {
							notificationsList += '<li>' +
												 '<a class="text-center">' +
												 '<span class="message">' +
										         'Você não tem nenhuma notificação' +
										         '</span>' +
										         '</a>' +
												 '</li>';
						}
						$('#menu2').html(notificationsList);
						// if IE start polling
						if (sciManagerCommons.isIEBrowser()) {
							setTimeout(notificationsCommons.getNotificationsCount, 10000);
						}
					})
					.fail(function () {}
					);
			}
		},

		markNotificationVisualized : function(e) {
			var clickTarget = $(e.target);
	    	var element = $(clickTarget).parents('li');
	    	var hrefValue = $(clickTarget).attr('href');
	    	var userId = $('#current-user-id').val();
	    	var messageNotificationId = $(e.target).attr('data-message-id');
	    	var paramMessage = messageNotificationId != undefined ? ('?notificationMessageId=' + messageNotificationId) : '';

	    	if (!$(clickTarget).hasClass('notification-visuazed')) {
		    	$.get(sciManagerCommons.getUrlContext(window.location) + '/notification-message/update-visualized/' + userId + paramMessage)
					.done(function (data) {
						if (sciManagerCommons.isSuccessType(data.type)) {
							if (!$(clickTarget).hasClass('visualize-all')) {
								$(element).removeClass('notification-not-visualized');
								$(clickTarget).addClass('notification-visuazed');
								$(clickTarget).addClass('hide');
							}
							else {
								$('.messages li').removeClass('notification-not-visualized');
								$('.mark-visualized').addClass('hide');

								if ($('#btn-visualize-all')) {
									$('#btn-visualize-all').addClass('disabled');
								}
							}
							notificationsCommons.getNotificationsCount();
							notificationsCommons.getNotificationsData();
			        	}
			        	else {
			        		sciManagerCommons.buildErrorMessage(data.text);
			        	}
					 })
					 .fail(function () {}
				 );
	    	}
	    	if (hrefValue) {
	    		window.location = hrefValue;
	    	}
		}
	}

}(jQuery);
