// Easy hover previews for images and flash

(function( $ ){

    $.fn.initPreview = function(extraHandler) {
        $('div[preview]').each(function(index) {
            $(this).mouseenter(function() {
                var preview = $('<div id="previewBox' + index + '" class="previewBox"/>');
                preview.css('position', 'absolute')
                .css('left', parseInt($(this).offset().left) + parseInt($(this).css('width')))
                .css('top', parseInt($(this).offset().top))
                .appendTo('body');
                
                if ($(this).attr('image')) {
                    preview.append('<img src="' + $(this).attr('image') + '"/>');
                } else if ($(this).attr('flash')) {

                    var height = $(this).attr('previewHeight');
                    var width = $(this).attr('previewWidth');
                    var src = $(this).attr('flash');
                    var obj = '<object type="application/x-shockwave-flash" id="swf13262196951" data="' + src + '" width="' + width + '" height="' + height + '"><param name="swliveconnect" value="default"><param name="play" value="true"><param name="loop" value="true"><param name="menu" value="false"><param name="quality" value="autohigh"><param name="scale" value="showall"><param name="wmode" value="opaque"><param name="bgcolor" value="#FFFFFF"><param name="allowfullscreen" value="false"><param name="allowscriptaccess" value="never"><param name="src" value="' + src + '"><param name="height" value="' + height + '"><param name="width" value="' + width + '"></object>';

                    preview.append(obj);
                } else if (extraHandler) {
                    extraHandler($(this), preview);
                }
            });
            
            $(this).mouseleave(function() {
                $('#previewBox' + index).remove();
            });             
        });
    };
    
})( jQuery );