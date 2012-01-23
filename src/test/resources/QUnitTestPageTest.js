$(document).ready(function(){
    
	test("a basic qunit example", function() {
	  ok( true, "this qunit is fine" );
	  var value = "hello";
	  equal( value, "hello", "We expect value to be hello" );

	});
	
	module("broken");
	
	test("a broken qunit", function(){
		  ok(false, "This is a qunit failure");
		  ok(false, "This is another qunit failure");
	});
});
