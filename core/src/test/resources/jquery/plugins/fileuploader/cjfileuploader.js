var qq = qq || {};

var CJFileUploader = function (){

	function uplodify(options){
		extendXhrHandler();
		extendUploadButtonCreation(options);	
		decorate(options);
		return new qq.FileUploaderBasic(options);
	}
	
	function decorate(options){
		options['multiple'] = false;
	}
	
	function extendXhrHandler(){
		qq.extend(qq.UploadHandlerXhr.prototype, {
			_onComplete: function(id, xhr){
			    if (!this._files[id]) return;
			    
			    var name = this.getName(id);
			    var size = this.getSize(id);
			    
			    this._options.onProgress(id, name, size, size);
			            
			    if (xhr.status == 200){
			        this.log("xhr - server response received");
			        this.log("responseText = " + xhr.responseText);
			                    
			        var response = xhr.responseText;
			        this._options.onComplete(id, name, response);
			    } else {                   
			        this._options.onComplete(id, name, {});
			    }
			            
			    this._files[id] = null;
			    this._xhrs[id] = null;    
			    this._dequeue(id);                    
			}});
	} 
	
	function extendUploadButtonCreation(options){
		qq.extend(qq.FileUploaderBasic.prototype, {
			_createUploadButton: function(element){
	        var self = this;
	        return new qq.UploadButton({
	        	hoverClass: "cj-uploader-hover",
	            element: element,
                name: options.name,
	            multiple: this._options.multiple && qq.UploadHandlerXhr.isSupported(),
	            onChange: function(input){
	                self._onInputChange(input);
	            }        
	        });           
	    	}
		});
	}

	return {
		uplodify:uplodify
	}
}();






