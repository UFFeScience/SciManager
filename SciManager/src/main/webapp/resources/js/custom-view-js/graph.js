$(function() {

	graphModule.init();

});

var graphModule = function($) {
	var graphBackup = '';

	return {

		init : function() {
			graphModule.bindUIEvents();
			graphModule.bindValidationEvents();
		},

		bindUIEvents : function() {
			// trigger add node to graph
			$('body').off('click', '.add-node').on('click', '.add-node', function() {
				graphModule.handleAddNodeToGraph(this);
			});

			// trigger add link to nodes of graph
			$('body').off('click', '.add-node-link').on('click', '.add-node-link', function() {
				graphModule.handleAddNodeLinkToGraph(this);
			});

			// trigger remove link of nodes of graph
			$('body').off('click', '.remove-node-link').on('click', '.remove-node-link', function() {
				graphModule.handleRemoveNodeLink(this);
			});

			// trigger remove node of graph
			$('body').off('click', '.remove-node').on('click', '.remove-node', function() {
				graphModule.handleRemoveNode(this);
			});

			// load destiny nodes to remove link
			$('body').off('change', '.origin-node-select-remove').on('change', '.origin-node-select-remove', function() {
				graphModule.handleLoadDestinyNodesToEdit();
			});

			// trigger confirm edit note in link
			$('body').off('click', '.edit-node-link').on('click', '.edit-node-link', function() {
				graphModule.handleConfirmEditLink(this);
			});

			// load destiny nodes to edit link
			$('body').off('change', '.origin-node-edit-select').on('change', '.origin-node-edit-select', function() {
				graphModule.handleLoadDestinyNodesToRemove();
			});

			// trigger edit link note
			$('body').off('change', '.destiny-node-edit-select').on('change', '.destiny-node-edit-select', function() {
				graphModule.handleEditLinkNote();
			});

			// trigger confirm edit nodeName
			$('body').off('click', '.edit-node').on('click', '.edit-node', function() {
				graphModule.handleConfirmEditNodeName(this);
			});

			// load node name to edit
			$('body').off('change', '.node-name-edit').on('change', '.node-name-edit', function() {
				graphModule.handleLoadNodeName();
			});

			// trigger close dropdown menu
			$('body').off('click', '.cancel-node-button').on('click', '.cancel-node-button', function() {
				graphModule.handleCloseDropdown(this);
			});

			// trigger cancel edit graph
			$('body').off('click', '#cancel-graph').on('click', '#cancel-graph', function() {
				graphModule.handleCancelEdition();
			});

			// trigger edit graph
			$('body').off('click', '#edit-graph').on('click', '#edit-graph', function() {
				graphModule.handleTriggerEditGraph();
			});

			// block special characters in graph
			$('body').off('input', '.mermaid-input').on('input', '.mermaid-input', function() {
				graphModule.blockSpecialChars(this);
			});

			// trigger confirm generate graph
			$('body').off('click', '#confirm-generate-graph-button').on('click', '#confirm-generate-graph-button', function() {
				graphModule.generateGraphFromModelFile();
			});

			// trigger load graph from model file
			$('body').off('click', '#generate-graph-button').on('click', '#generate-graph-button', function() {
				graphModule.handleLoadGraphFromModelFile();
			});
		},

		// Update graph
		updateGraph : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode !== undefined && graphCode !== null && graphCode.length > 0) {
				mermaid.init();
				graphFunctions.drawSVG();
				graphModule.updateSelects();
			}
		},

		// Update select of actions
		updateSelects : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var options = '';

				for (var i = 0; i < graphNodes.length; i++) {
					options += '<option value="' + graphNodes[i].nodeName + '">' + graphNodes[i].nodeName + '</option>\n';
				}

				$('.origin-node-select').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó de origem</option>\n' + options);
				$('.destiny-node-select').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó destino</option>\n' + options);
				$('.node-name-remove').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó</option>\n' + options);
				$('.origin-node-select-remove').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó de origem</option>\n' + options);
				$('.node-name-edit').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó de origem</option>\n' + options);
				$('.origin-node-edit-select').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó de origem</option>\n' + options);
			}
		},

		handleAddNodeToGraph : function(targetElement) {
			$('#add-node-form').parsley().validate();
	        if (graphModule.validateGraphData('#add-node-form') === false) {
	        	return;
	        }

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var nodeName = $('.new-node-name').val();
				var nodeType = $('.node-type-select').val();

				if (!graphFunctions.graphContainsNode(graphNodes, nodeName)) {
					graphCode = graphFunctions.addNodeToGraphCode(graphCode, graphNodes, nodeName, nodeType);

					$('#graph-editor').val(graphCode);
					graphModule.updateGraph();
				}
				else {
					sciManagerCommons.buildWarningMessage('Já existe nó com este nome.');
				}
			}
			else {
				var nodeName = $('.new-node-name').val();
				var nodeType = $('.node-type-select').val();
				var newGraphCode = graphFunctions.createGraphCodeFromScratch(nodeName, nodeType);

				$('#graph-editor').val(newGraphCode);
				graphModule.updateGraph();
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('#add-node-form')[0].reset();
		},

		handleAddNodeLinkToGraph : function(targetElement) {
			$('#add-link-form').parsley().validate();
	        if (graphModule.validateGraphData('#add-link-form') === false) {
	        	return;
	        }

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var originNodeName = $('.origin-node-select').val();
				var distinyNodeName = $('.destiny-node-select').val();
				var linkNote = $('.link-note').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);
				var distinyNode = graphFunctions.getNodeByName(graphNodes, distinyNodeName);

				if (!originNode || originNode === null || originNode === undefined ||
					!distinyNode || distinyNode === null || distinyNode === undefined) {

					sciManagerCommons.buildErrorMessage('Erro inesperado ao adicionar link em nós.');
					return;
				}

				if (!graphFunctions.nodeContainsLink(originNode, distinyNodeName)) {
					originNode = graphFunctions.addLinkToNode(linkNote, originNode, distinyNode);
				}
				else {
					sciManagerCommons.buildWarningMessage('Nó destino já contém link com nó de origem.');
				}

				$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
				graphModule.updateGraph();
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('#add-link-form')[0].reset();
		},

		handleRemoveNodeLink : function(targetElement) {
			$('#remove-link-form').parsley().validate();
	        if (graphModule.validateGraphData('#remove-link-form') === false) {
	        	return;
	        }

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var originNodeName = $('.origin-node-select-remove').val();
				var distinyNodeName = $('.destiny-node-select-remove').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);
				var distinyNode = graphFunctions.getNodeByName(graphNodes, distinyNodeName);

				if (!originNode || originNode === null || originNode === undefined ||
					!distinyNode || distinyNode === null || distinyNode === undefined) {

					sciManagerCommons.buildErrorMessage('Erro inesperado ao remover link em nós.');
					return;
				}

				var linkIndex = graphFunctions.getIndexOfLink(originNode, distinyNodeName);

				if (linkIndex != -1) {
					originNode.nodeLinks.splice(linkIndex, 1);
				}
				else {
					sciManagerCommons.buildWarningMessage('O link que você está tentando remover não existe.');
				}

				$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
				graphModule.updateGraph();
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('#remove-link-form')[0].reset();
		},

		handleRemoveNode : function(targetElement) {
			$('#remove-node-form').parsley().validate();
	        if (graphModule.validateGraphData('#remove-node-form') === false) {
	        	return;
	        }

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var nodeToRemoveName = $('.node-name-remove').val();
				var nodeToRemove = graphFunctions.getNodeByName(graphNodes, nodeToRemoveName);

				if (!nodeToRemove || nodeToRemove === null || nodeToRemove === undefined ) {
					sciManagerCommons.buildErrorMessage('Erro inesperado ao remover nó.');
					return;
				}

				var nodeIndex = graphFunctions.getIndexOfNode(graphNodes, nodeToRemoveName);

				if (nodeIndex != -1) {
					graphNodes.splice(nodeIndex, 1);
				}
				else {
					sciManagerCommons.buildErrorMessage('Erro inesperado ao remover nó.');
				}

				for (var i = 0; i < graphNodes.length; i++) {
					var currentNode = graphNodes[i];
					var linkIndex = graphFunctions.getIndexOfLink(currentNode, nodeToRemoveName);

					if (linkIndex != -1) {
						currentNode.nodeLinks.splice(linkIndex, 1);

						if (currentNode.nodeLinks.length == 0) {
							currentNode.nodeLinks = undefined;
						}
					}
				}
				$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
				graphModule.updateGraph();
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('#remove-node-form')[0].reset();
		},

		handleLoadDestinyNodesToEdit : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var originNodeName = $('.origin-node-select-remove').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);

				if (originNode && originNode !== undefined && originNode !== null &&
					originNode.nodeLinks && originNode.nodeLinks !== undefined) {

					var options = '<option disabled="disabled" value="nodeName" selected="selected">Selecione nó destino</option>';

					for (var i = 0; i < originNode.nodeLinks.length; i++) {
						options += '<option value="' + originNode.nodeLinks[i].destinyNodeName + '">' +
						originNode.nodeLinks[i].destinyNodeName + '</option>\n';
					}

					$('.destiny-node-select-remove').empty().append(options);
					return;
				}
			}
			$('.destiny-node-select-remove').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó destino</option>');
		},

		handleConfirmEditLink : function(targetElement) {
			$('#edit-link-form').parsley().validate();
			if (graphModule.validateGraphData('#edit-link-form') === false) {
				return;
			}

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var destinyNodeName = $('.destiny-node-edit-select').val();
				var originNodeName = $('.origin-node-edit-select').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);

				if (originNode && originNode !== undefined && originNode !== null &&
					originNode.nodeLinks && originNode.nodeLinks !== undefined) {

					var linkNote = $('.edited-link-note').val();

					for (var i = 0; i < originNode.nodeLinks.length; i++) {
						if (destinyNodeName === originNode.nodeLinks[i].destinyNodeName) {
							if (!linkNote || linkNote === undefined || linkNote === null) {
								originNode.nodeLinks[i].linkNote = undefined;
								originNode.nodeLinks[i].linkStatement = originNode.nodeIndex + '[' + originNode.nodeName + ']-->' +
																		 originNode.nodeLinks[i].destinyNodeIndex +
																		'[' + originNode.nodeLinks[i].destinyNodeName + ']';
							}
							else {
								originNode.nodeLinks[i].linkNote = linkNote;
								originNode.nodeLinks[i].linkStatement = originNode.nodeIndex + '[' + originNode.nodeName + ']-->' +
																		'|' + linkNote + '|' + originNode.nodeLinks[i].destinyNodeIndex +
																		'[' + originNode.nodeLinks[i].destinyNodeName + ']';
							}
						}
					}

					$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
					graphModule.updateGraph();
				}
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('.edited-link-note').prop('disabled', true);
			$('#edit-link-form')[0].reset();
		},

		handleLoadDestinyNodesToRemove : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var originNodeName = $('.origin-node-edit-select').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);

				if (originNode && originNode !== undefined && originNode !== null &&
					originNode.nodeLinks && originNode.nodeLinks !== undefined) {

					var options = '<option disabled="disabled" value="nodeName" selected="selected">Selecione nó destino</option>';

					for (var i = 0; i < originNode.nodeLinks.length; i++) {
						options += '<option value="' + originNode.nodeLinks[i].destinyNodeName + '">' +
						originNode.nodeLinks[i].destinyNodeName + '</option>\n';
					}

					$('.destiny-node-edit-select').empty().append(options);
					return;
				}
			}

			$('.destiny-node-edit-select').empty().append('<option disabled="disabled" value="nodeName" selected="selected">Selecione nó destino</option>');

			$('#edit-link-form').parsley().validate();
			graphModule.validateGraphData('#edit-link-form');
		},

		handleEditLinkNote : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var destinyNodeName = $('.destiny-node-edit-select').val();
				var originNodeName = $('.origin-node-edit-select').val();
				var originNode = graphFunctions.getNodeByName(graphNodes, originNodeName);

				if (originNode && originNode !== undefined && originNode !== null &&
					originNode.nodeLinks && originNode.nodeLinks !== undefined) {

					var linkNote = undefined;

					for (var i = 0; i < originNode.nodeLinks.length; i++) {
						if (destinyNodeName === originNode.nodeLinks[i].destinyNodeName) {
							linkNote = originNode.nodeLinks[i].linkNote;
						}
					}

					if (linkNote !== undefined) {
						$('.current-link-note').val(linkNote);
						$('.edited-link-note').val(linkNote);
					}

					$('.edited-link-note').prop('disabled', false);
				}
			}

			$('#edit-link-form').parsley().validate();
			graphModule.validateGraphData('#edit-link-form');
		},

		handleConfirmEditNodeName : function(targetElement) {
			$('#edit-node-form').parsley().validate();
			if (graphModule.validateGraphData('#edit-node-form') === false) {
				return;
			}

			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);
				var editedNodeName = $('.edited-node-name').val();
				var currentNodeName = $('.node-name-edit').val();

				if (graphFunctions.graphContainsNode(graphNodes, editedNodeName) && editedNodeName !== currentNodeName) {
					sciManagerCommons.buildWarningMessage('Já existe nó com este nome.');
					return;
				}
				var graphNode = graphFunctions.getNodeByName(graphNodes, currentNodeName);

				if (graphNode && graphNode !== undefined && graphNode !== null) {
					graphNode.nodeName = editedNodeName;

					if (graphNode.nodeLinks && graphNode.nodeLinks !== undefined) {

						for (var i = 0; i < graphNode.nodeLinks.length; i++) {
							if (!graphNode.nodeLinks[i].linkNote || graphNode.nodeLinks[i].linkNote === undefined ||
								graphNode.nodeLinks[i].linkNote === null) {

								graphNode.nodeLinks[i].linkStatement = graphNode.nodeIndex + '[' + editedNodeName + ']-->' +
																	   graphNode.nodeLinks[i].destinyNodeIndex +
																	   '[' + graphNode.nodeLinks[i].destinyNodeName + ']';
							}
							else {
								graphNode.nodeLinks[i].linkStatement = graphNode.nodeIndex + '[' + editedNodeName + ']-->' +
																	   '|' + graphNode.nodeLinks[i].linkNote + '|' +
																	   graphNode.nodeLinks[i].destinyNodeIndex +
																	   '[' + graphNode.nodeLinks[i].destinyNodeName + ']';
							}
						}
					}

					for (var i = 0; i < graphNodes.length; i++) {
						if (graphNodes[i].nodeLinks && graphNodes[i].nodeLinks !== undefined) {
							for (var j = 0; j < graphNodes[i].nodeLinks.length; j++) {
								if (graphNodes[i].nodeLinks[j].destinyNodeName === currentNodeName) {
									graphNodes[i].nodeLinks[j].destinyNodeName = editedNodeName;

									if (!graphNodes[i].nodeLinks[j].linkNote || graphNodes[i].nodeLinks[j].linkNote === undefined ||
										graphNodes[i].nodeLinks[j].linkNote === null) {

										graphNodes[i].nodeLinks[j].linkStatement = graphNodes[i].nodeIndex + '[' + graphNodes[i].nodeName + ']-->' +
																				   graphNodes[i].nodeLinks[j].destinyNodeIndex +
																				   '[' + editedNodeName + ']';
									}
									else {
										graphNodes[i].nodeLinks[j].linkStatement = graphNodes[i].nodeIndex + '[' + graphNodes[i].nodeName + ']-->' +
																				   '|' + graphNodes[i].nodeLinks[j].linkNote + '|' +
																				   graphNodes[i].nodeLinks[j].destinyNodeIndex +
																				   '[' + editedNodeName + ']';
									}
								}
							}
						}
					}
					$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
					graphModule.updateGraph();
				}
			}

			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('.edited-node-name').prop('disabled', true);
			$('#edit-node-form')[0].reset();
		},

		handleLoadNodeName : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode && graphCode != null && graphCode !== undefined) {
				var graphNodes = graphFunctions.getGraphNodes(graphCode);

				var currentNodeName = $('.node-name-edit').val();
				var graphNode = graphFunctions.getNodeByName(graphNodes, currentNodeName);

				if (graphNode && graphNode !== undefined && graphNode !== null) {
					$('.current-node-name').val(graphNode.nodeName);
					$('.edited-node-name').val(graphNode.nodeName);
					$('.edited-node-name').prop('disabled', false);
				}
			}

			$('#edit-node-form').parsley().validate();
			graphModule.validateGraphData('#edit-node-form');
		},

		handleCloseDropdown : function(targetElement) {
			$(targetElement).closest(".dropdown-menu").prev().dropdown("toggle");
			$('#add-node-form').parsley().reset();
			$('#add-link-form').parsley().reset();
			$('#edit-node-form').parsley().reset();
			$('#edit-link-form').parsley().reset();
			$('#remove-link-form').parsley().reset();
			$('#remove-node-form').parsley().reset();

			$('#add-node-form')[0].reset();
			$('#add-link-form')[0].reset();
			$('#edit-node-form')[0].reset();
			$('#edit-link-form')[0].reset();
			$('#remove-link-form')[0].reset();
			$('#remove-node-form')[0].reset();

			$('.edited-node-name').prop('disabled', true);
			$('.edited-link-note').prop('disabled', true);
		},

		handleCancelEdition : function() {
			$('.graph-actions').slideToggle('fast');

			$('#graph-container').addClass('static-graph');
			$('#graph-container').removeClass('editable-graph');

			$('#cancel-graph').hide();
			$('#save-macro-graph').hide();
			$('#save-detailed-graph').hide();
			$('#edit-graph').show();

			$('#graph-editor').val(graphBackup);

			if (graphBackup === '') {
				$('#graph-container').html('');
			}
			else {
				graphModule.updateGraph();
			}

			graphBackup = ''
		},

		handleTriggerEditGraph : function() {
			$('.graph-actions').slideToggle('fast');

			graphBackup = $('#graph-editor').val();

			$('#graph-container').removeClass('static-graph');
			$('#graph-container').addClass('editable-graph');

			$('#save-detailed-graph').show();
			$('#save-macro-graph').show();
			$('#edit-graph').hide();
			$('#cancel-graph').show();
		},

		blockSpecialChars : function(targetElement) {
			var c = targetElement.selectionStart,
			r = /[^a-z0-9\,\+\-\.\,\ ]/gi,
			v = $(targetElement).val();

			if(r.test(v)) {
				$(targetElement).val(v.replace(r, ''));
				c--;
			}

			targetElement.setSelectionRange(c, c);
		},

		// Get parsed model file and generate graph
		generateGraphFromModelFile : function() {
			var workflowId = $('#workflow-id').val();

			$.get(sciManagerCommons.getUrlContext(window.location) + '/workflow/model-file/' + workflowId + '/json')
		        .done(function(data) {
		        	if (data !== null && data && '' !== data) {
		        		if ($('#info-message-trigger') && $('#info-message-trigger').hasClass('hasInfoMessage')) {
		        			PNotify.removeAll();
		        		}

		        		var graphNodes = graphFunctions.buildGraphFromModelFile(data);

		        		if (!graphNodes || graphNodes === null || graphNodes === undefined || graphNodes.length === 0) {
		        			sciManagerCommons.buildWarningMessage("Não foi possível construir grafo a partir de arquivo modelo. Arquivo mal formatado, utilize o editor para construir um grafo.");
		        			return;
		        		}

		        		$('#graph-editor').val(graphFunctions.generateGraphCodeFromGraphNodes(graphNodes));
		        		graphModule.updateGraph();
		        	}
		        	else {
		        		sciManagerCommons.buildErrorMessage("Erro ao construir grafo a partir de arquivo modelo.");
		        	}

					sciManagerCommons.unlockActionButton($('#confirm-generate-graph-button'));
		        })
		    	.fail(function(data) {
		    		sciManagerCommons.buildErrorMessage('Erro inesperado ao salvar grafo do workflow.');
					sciManagerCommons.unlockActionButton($('#confirm-generate-graph-button'));
		    	});
		},

		validateGraphData : function(selector) {
			if (true === $(selector).parsley().isValid()) {
	            $('.bs-callout-info').removeClass('hidden');
	            $('.bs-callout-warning').addClass('hidden');
	            return true;
	        }
	        else {
		        $('.bs-callout-info').addClass('hidden');
		        $('.bs-callout-warning').removeClass('hidden');
		        return false;
	        }
		},

		// Validation
		bindValidationEvents : function() {
			$.listen('parsley:field:validate', function() {
		        graphModule.validateGraphData('#add-node-form');
		        graphModule.validateGraphData('#add-link-form');
		        graphModule.validateGraphData('#edit-node-form');
		        graphModule.validateGraphData('#edit-link-form');
		        graphModule.validateGraphData('#remove-link-form');
		        graphModule.validateGraphData('#remove-node-form');
		    });
		},

		handleLoadGraphFromModelFile : function() {
			var graphCode = $('#graph-editor').val();

			if (graphCode !== null && graphCode !== '' && graphCode && graphCode !== undefined) {
				$('.bs-generate-graph-modal-lg').modal('show');
			}
			else {
				graphModule.generateGraphFromModelFile();
			}
		}
	}

}(jQuery);
