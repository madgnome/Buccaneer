define('buccaneer', ['jquery', 'underscore', 'eve', 'util/event', 'util/ajax', 'injector', 'exports'],
function($, _, eve, event, ajax, injector, exports) {

  function retrieveTagsForType(type, target) {
    var dialog = $('#inline-dialog-' + type);

    if (dialog.length === 0) {
      var params = {
        type: type,
        path: ''
      };

      ajax.rest({
        url: AJS.contextPath() + '/rest/buccaneer/latest/tags',
        type: 'GET',
        data: params
      }).then(function() {

      }).done(function(data) {
        var TagList = require('widget/tag-list');
        new TagList(target, type, data.tags);
      });
    }
  }

  function onClick(event, type) {
    if (event.ctrlKey) {
      retrieveTagsForType(type, event.target);
    }
  }

  eve.on('stash.feature.sourceview.dataLoaded', function(start, limit, data) {
    injector.injectInHtml($('code'), '<span class="buccaneer-symbol" onclick="require(\'buccaneer\').onClick(event, \'$&\')">$&</span>');
    $('.buccaneer-symbol').hover(function(e) {
      if (e.ctrlKey) {
        $(this).addClass('hover');
      }
    }, function(e) {
      $(this).removeClass('hover');
    });
  });

  exports.onClick = onClick;
});

require('buccaneer');
