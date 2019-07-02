$(function() {

	popoverCommons.init();

});

var popoverCommons = function() {

	return {

		init : function() {
			// build popovers
			popoverCommons.bindUIEvents();
		},

		initializePopovers : function() {
			popoverCommons.buildDetailsPopover();
		    popoverCommons.buildExecutionDetailsPopover();
		    popoverCommons.buildWorkflowDetailsPopover();
		},

		bindUIEvents : function() {
			// close popovers on button click
			$('body').off('click', '.details-button').on('click', '.details-button', function() {
		    	popoverCommons.handleClosePopoverOnButtonClick();
		    });

			// close popovers on button click
			$('body').off('click', '.execution-details-button')
					 .on('click', '.execution-details-button', function() {
		    	popoverCommons.handleClosePopoverOnButtonClick();
		    });

			// close popovers on button click
			$('body').off('click', '.workflow-details-button')
					 .on('click', '.workflow-details-button', function() {
				popoverCommons.handleClosePopoverOnButtonClick();
		    });

			// close popovers on button click
			$('body').off('click', '.configure-button').on('click', '.configure-button', function() {
				popoverCommons.handleClosePopoverOnButtonClick();
			});

			// close popovers on button click
		    $('body').off('click', '.btn-status').on('click', '.btn-status', function() {
		    	popoverCommons.handleClosePopoverOnButtonClick();
		    });

			// close popovers on cancel click
			$('body').off('click', '.cancel-configure-swfms-button')
					 .on('click', '.cancel-configure-swfms-button', function() {
				popoverCommons.hidePopovers();
			});

			// close popovers on cancel click
		    $('body').off('click', '.cancel-edit-status-button')
					 .on('click', '.cancel-edit-status-button', function() {
		    	popoverCommons.hidePopovers();
			});

			// trigger close all popovers
		    $('body').click(function(e) {
		    	popoverCommons.hidePopoversOnOutsideClick(e);
		    });
		},

		hidePopoversOnOutsideClick : function(e) {
			$('body').on('mouseup', function(e) {
	    	    if (!$(e.target).closest('.popover').length) {
	    	    	popoverCommons.hidePopovers();
	    	    }
	    	});
		},

		handleClosePopoverOnButtonClick : function() {
			$('.popover').each(function() {
				if (!$(this).hasClass('in')) {
					$(this).popover('hide');
				}
			});
		},

		// trigger details popover
		buildDetailsPopover : function() {
		    $('.details-button').each(function() {
		    	var taskId = $(this).attr('id').replace('details-button-', '');

		    	$(this).popover({
		    		html: true,
		    		container: 'body',
		    		mode: 'popup',
		    		placement: 'auto',
		    		title: function() {
		    			return $('#task-details-' + taskId + ' .popover-description-head').html();
		    		},
		    		content: function() {
		    			return $('#task-details-' + taskId + ' .popover-description-content').html();
		    		}
		    	});
		    });
		},

		// trigger workflow execution details popover
		buildExecutionDetailsPopover : function() {
		    $('.execution-details-button').each(function() {
		    	var workflowExecutionId = $(this).attr('id').replace('execution-details-button-', '');

		    	var popover = $(this).popover({
		    		html: true,
		    		container: 'body',
		    		mode: 'popup',
		    		placement: 'auto',
		    		title: function() {
		    			return $('#workflow-execution-' + workflowExecutionId + ' .popover-description-head').html();
		    		},
		    		content: function() {
		    			return $('#workflow-execution-' + workflowExecutionId + ' .popover-description-content').html();
		    		}
		    	});

		    	popover.on('show.bs.popover', function() {
		    		popover.data('bs.popover').tip().css({'max-width': '400px'});
		    	});
		    });
		},

		// trigger workflow details popover
		buildWorkflowDetailsPopover : function() {
		    $('.workflow-details-button').each(function() {
		    	var workflowId = $(this).attr('id').replace('workflow-details-button-', '');

		    	$(this).popover({
		    		html: true,
		    		container: 'body',
		    		mode: 'popup',
		    		placement: 'auto',
		    		title: function() {
		    			return $('#workflow-details-' + workflowId + ' .popover-description-head').html();
		    		},
		    		content: function() {
		    			return $('#workflow-details-' + workflowId + ' .popover-description-content').html();
		    		}
		    	});
		    });
		},

		// hide popovers
		hidePopovers : function() {
	        $('.popover').each(function() {
	            $(this).popover('hide');
	        });
	    }
	}

}();
