/*
 * Copyright 2009 Västa Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
function readspeaker(rs_call, rs_file_name)
{
	savelink=rs_call+"&save=1&audiofilename="+rs_file_name;
	rs_call=rs_call+"&output=audio";                        
	rs_call=escape(rs_call);                                
	start_rs_table="<table style='border:1px solid #aeaeae; font-size: 10px;'><tr><td>";
	rs_embed="<object type='application/x-shockwave-flash' data='http://media.readspeaker.com/flash/readspeaker19.swf?mp3="+rs_call+"&autoplay=1&rskin=bump' height='20' width='250'><param name='movie' value='http://media.readspeaker.com/flash/readspeaker19.swf?mp3="+rs_call+"&autoplay=1&rskin=bump'><param name='quality' value='high'><param name='SCALE' value='exactfit'><param name='wmode' value='transparent'><embed wmode='transparent' src='http://media.readspeaker.com/flash/readspeaker19.swf?mp3="+rs_call+"&autoplay=1&rskin=bump' quality='high' pluginspage='http://www.macromedia.com/go/getflashplayer' type='application/x-shockwaveflash' scale='exactfit' height='20' width='250'></embed></object>"; 
	rs_downloadlink="<br>Speech-enabled by <a href='http://www.readspeaker.com'>ReadSpeaker</a><br><a href='"+savelink+"'>Download audio</a>";               
	close_rs="<br /><a href='#' onclick='close_rs_div(); return false;'>Close window</a>"; 
	end_rs_table="</td></tr></table>";                                      
	var x=document.getElementById('rs_div');        
	x.innerHTML=start_rs_table+rs_embed+rs_downloadlink+close_rs+end_rs_table;
}

function close_rs_div()
{
	var x=document.getElementById('rs_div');        
	x.innerHTML="";
}

function printElementLTH(element) {
	var a = window.open('','','width=800,height=600');
	a.document.open("text/html");
	a.document.write('<html><head><script type="text/javascript" src="resources/scripts/utilities.js" ></script><script type="text/javascript" src="resources/scripts/tabview-min.js" ></script><script type="text/javascript" src="resources/scripts/script.js" ></script><link href="resources/styles/lth/style.css" title="compact" rel="stylesheet" type="text/css" /><link href="resources/styles/main.css" title="compact" rel="stylesheet" type="text/css" />');
	// Hide directions row
	a.document.write('<style type="text/css">#plan-trip-row1, #plan-trip-row2 {display: none;}</style>'); 
	a.document.write('</head><body>');
	a.document.write(document.getElementById(element).innerHTML);
	a.document.write('</body></html>');
	a.document.close();
	a.print();
}