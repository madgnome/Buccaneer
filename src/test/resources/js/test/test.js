Qunit.require("lib/jquery-1.7.1.js");
Qunit.require('lib/underscore.js');
Qunit.require('lib/require-lite.js');
Qunit.require("lib/eve.js");
Qunit.require('lib/sinon-1.2.0.js');
Qunit.require('lib/sinon-qunit-1.0.0.js');

Qunit.require('injector');
var injector = require('injector');
var $ = require('jquery');

var TestCases = {};

TestCases['simple-java-file'] = {
  html: [
    '<pre><span class="keyword">package</span> com.madgnome.jira.plugins.jirachievements.utils.data;<br></pre>',
    '<pre><br></pre>',
    '<pre><span class="keyword">public</span> <span class="class"><span class="keyword">class</span> <span class="title">AOUtil</span><br></span></pre>',
    '<pre><span class="class">{</span><br></pre>',
    '<pre>  <span class="keyword">public</span> <span class="keyword">static</span> <span class="keyword">final</span> String TABLE_PREFIX = <span class="string">"AO_7A05D7"</span>;<br></pre>',
    '<pre><br></pre>',
    '<pre>  <span class="keyword">public</span> <span class="keyword">static</span> String getTablePrefix()<br></pre>',
    '<pre>  {<br></pre>',
    '<pre>    <span class="keyword">return</span> TABLE_PREFIX;<br></pre>',
    '<pre>  }<br></pre>',
    '<pre>}<br></pre>'
  ].join(''),
  lines: [
    "package com.madgnome.jira.plugins.jirachievements.utils.data;",
    "",
    "public class AOUtil",
    "{",
    "  public static final String TABLE_PREFIX = \"AO_7A05D7\";",
    "",
    "  public static String getTablePrefix()",
    "  {",
    "    return TABLE_PREFIX;",
    "  }"
    , "}"
  ],
  resultHtml: [
    '<pre><span class="keyword">package</span> <a>com</a>.<a>madgnome</a>.<a>jira</a>.<a>plugins</a>.<a>jirachievements</a>.<a>utils</a>.<a>data</a>;<br></pre>',
    '<pre><br></pre>',
    '<pre><span class="keyword">public</span> <span class="class"><span class="keyword">class</span> <span class="title"><a>AOUtil</a></span><br></span></pre>',
    '<pre><span class="class">{</span><br></pre>',
    '<pre>  <span class="keyword">public</span> <span class="keyword">static</span> <span class="keyword">final</span> <a>String</a> <a>TABLE_PREFIX</a> = <span class="string">"AO_7A05D7"</span>;<br></pre>',
    '<pre><br></pre>',
    '<pre>  <span class="keyword">public</span> <span class="keyword">static</span> <a>String</a> <a>getTablePrefix</a>()<br></pre>',
    '<pre>  {<br></pre>',
    '<pre>    <span class="keyword">return</span> <a>TABLE_PREFIX</a>;<br></pre>',
    '<pre>  }<br></pre>',
    '<pre>}<br></pre>'
  ].join('')
};

test("Inject ", function() {
  var testCase = TestCases['simple-java-file'];

  var transformedHtml = injector.injectInHtml($('<div/>').html(testCase.html), '<a>$&</a>');

  equal(transformedHtml, testCase.resultHtml);
});
