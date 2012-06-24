define('injector', ['jquery', 'exports'], function($, exports) {
  var ignoredClassName = {'keyword': 1, 'string': 1};

  function traverseChildNodes(node, replacementPattern, ignored) {
    var next;
    if (ignoredClassName.hasOwnProperty(node.className)) {
      ignored = true;
    }

    if (node.nodeType === 1) {
      // (Element node)
      if (node = node.firstChild) {
        do {
          // Recursively call traverseChildNodes
          // on each child node
          next = node.nextSibling;
          traverseChildNodes(node, replacementPattern, ignored);
        } while(node = next);
      }

    } else if (node.nodeType === 3) {
      // (Text node)
      if (!ignored) {
        wrapMatchesInNode(node, replacementPattern);
      }
    }

    ignored = false;
  }

  function wrapMatchesInNode(textNode, replacementPattern) {

    var temp = document.createElement('div');

    temp.innerHTML = textNode.data.replace(/[\w]+/g, replacementPattern);

    while (temp.firstChild) {
      textNode.parentNode.insertBefore(temp.firstChild, textNode);
    }

    // Remove original text-node:
    textNode.parentNode.removeChild(textNode);

  }

  function injectInHtml($html, replacementPattern) {

    $html.each(function() {
      traverseChildNodes(this, replacementPattern, false);
    });


    return $html.html();
  }

  exports.injectInHtml = injectInHtml;
});
