define('widget/tag-list',
  ['jquery', 'underscore', 'eve', 'util/navbuilder', 'page/util/pageUtil'],
  function($, _, eve, navBuilder, pageUtil) {

    function TagList(tagListSelector, symbol, tags) {
      var _id = symbol,
          _tags = tags;
      var $tagListTarget = this.$_tagListTarget = $(tagListSelector);

      var self = this;
      var showOnClick = function(e) {
          if (e.ctrlKey) {
            self.show(e);
            e.preventDefault();
          }
        },
        itemClicked = this._itemClicked = function(e) {
          self.hide();

          var $li = $(this),
              path = $li.attr('data-path'),
              line = $li.attr('data-line');

          // Go to location
          var urlBuilder = navBuilder
                  .project(pageUtil.getProjectKey())
                  .repo(pageUtil.getRepoSlug())
                  .browse()
                  .path(path.split('/'));
          window.location = urlBuilder.build() + '#' + line;

          e.preventDefault();
        };

      var _dialogInitialised,
          $currentContent;

      var onShowDialog = function($content, trigger, showPopup) {
        if (!_dialogInitialised) {
          _dialogInitialised = true;

          $currentContent = $content.html(raid.widget.tagList({tags: _tags}));
          $currentContent.on('click', 'li.tag', itemClicked);

          showPopup();
        } else {
          showPopup();
        }
      };

      this._inlineDialog = AJS.InlineDialog($tagListTarget, _id, onShowDialog, {
        hideDelay: null,
        noBind: true,
        width: 400
      });

      $tagListTarget.click(showOnClick);
      self.show();
    }

    TagList.prototype.destroy = function () {
      this._inlineDialog.remove();
    };

    TagList.prototype.show = function() {
      this._inlineDialog.show();
    };

    TagList.prototype.hide = function() {
      this._inlineDialog.hide();
    };

    return TagList;
  }
);
