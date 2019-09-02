/** * Created by marszed on 16/3/10. */define(function(require, exports, module) {	var template = require('template'),		$ = require('jquery'),		common = require('common'),		global = require('global'),		layer = require('layer'),		zclip = require('zclip'),		intInfo = require('intInfo'),		int = new intInfo.intInfoInit(),		$sliderRight = $('#sliderRight'),		GiGoldPay = new common.GiGoldPay(),		GiGoldTool = new global.GiGoldTool();	var $bdLeft = $('#bdLeft'),		resResLock = false;		resCodeLock = false,//响应吗锁		saveModuleLock = false,//依赖保存锁		sendModuleLock = false,//发送锁		delModuleLock = false;//依赖删除锁	//添加用例信息	$('#addUsecase').click(function(){		var ifId = $(this).attr('ifId');		if(ifId){			getResCode(ifId);		} else {			layer.tips('请先选择接口',$(this));		}	});	//请求,响应参数修改	$bdLeft.on('click','.change-data',function(){		var $textarea = $(this).parent().siblings('.over-box').find('.area');		if($textarea.attr('contenteditable') == 'false'){			$textarea.removeAttr('readonly').toggleClass('readonly-bg').attr('contenteditable','true');		}else{			$textarea.toggleClass('readonly-bg').attr({'contenteditable':'false','readonly':'true'});		}	});	//请求,响应参数保存	$bdLeft.on('click','.save-data',function(){		var $this = $(this),			reqHtml = $('#requestJson').html(),			reqPath = $('#reqPath').val(),			resHead = $('#reqHead').html(),			resHtml = $('#responseJson').html();		var saveData = {			caseName: $this.attr('caseName'),			checkJson: $this.attr('checkJson'),			id: $this.attr('caseId')+'',			ifId: $this.attr('ifId')+'',			requestJson: reqHtml || '',//请求体			responseJson: resHtml || '',//响应题			rspRefJson: $this.attr('rspRefJson')+'',			requestPath: reqPath,//请求路径			requestHead: resHead//请求头		};		if(!resResLock){			resResLock = true;			GiGoldPay.ajaxHandler({				"url": GiGoldPay.ipBullShit()+"/autotest/updateifsysmock.do",				"data": saveData,				"onSuccess":function(data) {					if (data.rspCd == "00000") {						resResLock = false;						layer.msg('保存信息成功');					} else {						layer.msg('保存信息失败');						GiGoldPay.cancleLock(sendCaseLock);					}				},				"onError":function(data) {					layer.msg('保存信息失败');					GiGoldPay.cancleLock(resResLock);				}			});		}	});	//报文依赖新增,删除操作	$bdLeft.on('click','.add-module, .del-module',function(){		var $this = $(this);		if($this.hasClass('add-module')){			if(!$this.parents('h4').next().find('#msgModule').html()){				layer.tips('请先选择用例!',$this);				return false;			}			var copyHtml = '<tr>' +				'<td class="form-group"><input type="text" class="form-control" placeholder="请输入占位符"></td>' +				'<td><div class="btn-group moduleSelect">'+$this.parents('h4').next().find('.moduleSelect').html()+'</div></td>'+				'<td><div class="btn-group moduleSelect">'+$this.parents('h4').next().find('.objectSelect').html()+'</div></td>'+				'<td class="form-group">'+				'<input type="text" class="form-control" placeholder="请输入依赖字段">'+				'</td>'+				'<td><button type="button" class="del-module btn btn-warning iconfont">&#xe657;</button></td>'+				'</tr>';			$this.parents('h4').next().find('#msgModule').append(copyHtml);		} else {			layer.confirm('确认删除报文依赖?', {				title: '删除提示',				btn: ['确定','取消'] //按钮			}, function(){				var delid = $this.parents('tr').find('.caret').attr('delid');				if(!delModuleLock && delid){					delModuleLock = true;					GiGoldPay.ajaxHandler({						"url": GiGoldPay.ipBullShit()+"/autotest/deleteFieldRefer.do",						"data": {							'id': delid+''						},						"onSuccess":function(_data) {							if (_data.rspCd == "00000") {								$this.closest('tr').remove();								layer.msg('删除成功');								delModuleLock = false;							} else {								layer.msg('删除失败,请重试');								GiGoldPay.cancleLock(delModuleLock);							}						},						"onError":function(data) {							GiGoldPay.cancleLock(delModuleLock);						}					});				} else {					layer.tips('缺少依赖ID',$(this));				}			}, function(){				//用户取消删除用例操作			});		}	});	//报文依赖新增依赖	$bdLeft.on('click','.moduleSelect > li',function(){		var $this = $(this),			char = $this.find('a').attr('char');		$this.parent().siblings('button').html($this.find('a').html()+'&nbsp;<span class="caret" mockid='+$this.find('a').attr('mockid')+' refmockid='+$this.find('a').attr('refmockid')+'></span>');	});	//引用对象选择	$bdLeft.on('click','.objSelect > li',function(){		var $this = $(this),			char = $this.find('a').attr('char');		$this.parent().siblings('button').html($this.find('a').html()+'&nbsp;<span class="caret" type='+$this.find('a').attr('type')+'></span>');	});	//报文依赖保存	$bdLeft.on('click','#saveModule',function(){		var data = [];		var $tr = $('#msgModule').find('tr');		for(var i = 0, len = $tr.length; i < len; i++){			var temp  = $($tr[i]);			if(temp.find('input').eq(0).val() && temp.find('input').eq(1).val() && temp.find('.caret').attr('mockid') && temp.find('.caret').attr('refmockid')){				var id = temp.attr('msgid');				if(id){					//更新报文依赖					data.push({						'id': id+'',						'mockid': temp.find('.caret').attr('mockid') - 0,						'ref_mock_id': temp.find('.caret').eq(0).attr('refmockid') - 0,//依赖接口id						'ref_feild': temp.find('input').eq(1).val() + '',//依赖字段						'alias': temp.find('input').eq(0).val() + '',//占位符						'type': temp.find('.caret').eq(1).attr('type') + '',//引用对象类型						'status': 'Y'					});				}else{					//新增报文依赖					data.push({						'mockid': temp.find('.caret').attr('mockid') - 0,						'ref_mock_id': temp.find('.caret').eq(0).attr('refmockid') - 0,//依赖接口id						'ref_feild': temp.find('input').eq(1).val() + '',//依赖字段						'alias': temp.find('input').eq(0).val() + '',//占位符						'type': temp.find('.caret').eq(1).attr('type') + '',//引用对象类型						'status': 'Y'					});				}			}else{				layer.msg('请正确填写新增报文依赖');				return false;			}		}		if(!saveModuleLock){			saveModuleLock = true;			GiGoldPay.ajaxHandler({				"url": GiGoldPay.ipBullShit()+"/autotest/addFieldRefer.do",				"data": {					"referList": data				},				"onSuccess":function(_data) {					if (_data.rspCd == "00000") {						layer.msg('保存成功');						saveModuleLock = false;					} else {						layer.msg('保存失败');						GiGoldPay.cancleLock(saveModuleLock);					}				},				"onError":function(data) {					GiGoldPay.cancleLock(saveModuleLock);				}			});		}	});	//发送autotest	$bdLeft.on('click','#sendModule',function(){		var $this = $(this),			mockId = $this.attr('mockid');		if(!mockId){			layer.tips('缺少用例id',$this);			return false;		}		if(!sendModuleLock){			sendModuleLock = true;			$this.removeClass('btn-success').find("i").html('&nbsp;');			$this.addClass('btn-disable');			GiGoldPay.ajaxHandler({				"url": GiGoldPay.ipBullShit()+"/autotest/autotest.do",				"data": {					"mockId": mockId-0				},				"onSuccess":function(data) {					if (data.rspCd == "00000") {						sendModuleLock = false;						$this.removeClass('btn-disable');						$this.addClass('btn-success').find("i").html('&#xe67e;');						layer.tips('执行成功',$this);						//刷新当前用例信息						$('#interfaceInfo').find('.ct-active').click();					}else{						layer.tips('执行失败,2s后可重试',$this);						setTimeout(function(){							sendModuleLock = false;							$this.removeClass('btn-disable');							$this.addClass('btn-success').find("i").html('&#xe67e;');						},2000);					}				},				"onError":function(data) {					layer.tips('执行出错,2s后可重试',$this);					setTimeout(function(){						sendModuleLock = false;						$this.removeClass('btn-disable');						$this.addClass('btn-success').find("i").html('&#xe67e;');					},2000);				}			});		}	});	var reqRes = null;	//请求参数,响应参数插入占位符	$bdLeft.on('click','.reqInsertChar > li',function(){		if(reqRes == null){			layer.msg('请先点击请求体,或响应报文');			return false;		}		var $this = $(this),			char = $this.find('a').attr('char');		$this.parent().siblings('button').html($this.find('a').html()+'&nbsp;<span class="caret"></span>');		if(reqRes == 'req'){			insertChar({				dom: $('#requestJson'),				char: char			});		} else if(reqRes == 'res'){			insertChar({				dom: $('#responseJson'),				char: char			});		} else if(reqRes == 'head'){			insertChar({				dom: $('#reqHead'),				char: char			});		} else if(reqRes == 'path'){			add(char,'reqPath');		}	});	$bdLeft.on('click','#reqHead',function(e){		reqRes = 'head';	});	$bdLeft.on('click','#requestJson',function(e){		reqRes = 'req';	});	$bdLeft.on('click','#responseJson',function(e){		reqRes = 'res';	});	$bdLeft.on('keyup keydown mousedown mouseup focus click','#reqPath',function(e){		if(e.type == 'click'){			reqRes = 'path';		}		savePos(this);	});	//插入占位符	function insertChar(data){		var obj= data.dom;		var range, node;		if(!obj.is(":focus")) {			obj.focus();		}		if(window.getSelection && window.getSelection().getRangeAt) {			range = window.getSelection().getRangeAt(0);			range.collapse(false);			node = range.createContextualFragment(data.char);			var c = node.lastChild;			range.insertNode(node);			if(c){				range.setEndAfter(c);				range.setStartAfter(c)			}			var j = window.getSelection();			j.removeAllRanges();			j.addRange(range);		} else if (document.selection && document.selection.createRange) {			document.selection.createRange().pasteHTML(data.char);		}	}	//textarea光标定位插入字符	var start=0;	var end=0;	function MoveCursortoPos(id,pos){//定位dom,光标位置		var obj = document.getElementById(id);		pos = pos ? pos : obj.value.length;		if (obj.createTextRange) {//for IE			var range = obj.createTextRange();			range.moveStart("character", pos);			range.collapse(true);			range.select();		} else {//for !IE			obj.setSelectionRange(obj.value.length, pos);		}		obj.focus();	}	function add(char,id){//插入字符,插入dom对象		var textBox = document.getElementById(id);		var pre = textBox.value.substr(0, start);		var post = textBox.value.substr(end);		textBox.value = pre + char + post;		MoveCursortoPos(id,start+char.length);	}	function savePos(textBox){//dom对象		if(typeof(textBox.selectionStart) == "number"){ //Firefox(1.5)			start = textBox.selectionStart;			end = textBox.selectionEnd;		} else if(document.selection){ //for IE			var range = document.selection.createRange();			if(range.parentElement().id == textBox.id){				//创建一个整个文本选择				var range_all = document.body.createTextRange();				range_all.moveToElementText(textBox);				//两个range，一个是已经选择的text(range)，一个是整个textarea(range_all)				//range_all.compareEndPoints()比较两个端点，如果range_all比range更往左，则返回小于0的值				// 否则range_all往右移一点，直到两个range的start相同。				//计算起始点的选择range_all运动开始开始的范围				for (start=0; range_all.compareEndPoints("StartToStart", range) < 0; start++){					range_all.moveStart('character', 1);				}				//从文本换行开始选择开始数，将它们添加到start				// 计算一下\n				for (var i = 0; i <= start; i ++){					if (textBox.value.charAt(i) == '\n')						start++;				}				var range_all = document.body.createTextRange();				range_all.moveToElementText(textBox);				for (end = 0; range_all.compareEndPoints('StartToEnd', range) < 0; end ++){					range_all.moveStart('character', 1);				}				for (var i = 0; i <= end; i ++){					if (textBox.value.charAt(i) == '\n')						end ++;				}			}		}	}	function useCaseDetail(){}	//fixUpdateTable.useCaseDetail({});	module.exports = {		useCaseDetail: useCaseDetail	};});