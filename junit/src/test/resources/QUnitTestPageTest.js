$(document).ready(function(){
    
	test("a basic qunit example", function() {
	  ok( true, "this qunit is fine" );
	  var value = "hello";
	  equal( value, "hello", "We expect value to be hello" );
	  ok( true, "this is also fine");
	});
	
	module("broken");
	
	test("a broken qunit", function(){
		  ok(true, "This should be okay though");
		  ok(false, "This is a qunit failure");
		  ok(false, "This is another qunit failure");
	});
});
