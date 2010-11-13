$(function(){
	loadPosts();
	
});

function setHash(path)
{
	if (currentLocation != path)
	{
		currentLocation = path;
		window.location.hash = path;
	}
}
var currentLocation = "";

setInterval(100, function()
{
	if (currentLocation != window.location.hash)
	{
		//do something
	}
})

function clearNav()
{
	$("#nav").empty();
}

function shownavWaiting()
{
	$("#nav").hide();
	$("#navloading").show(200);
}

function hidenavWaiting()
{
	$("#nav").show(200);
	$("#navloading").hide(200);
}

function clearContent()
{
	$("#content").empty();
}

function showWaiting()
{
	$("#content").hide();
	$("#loading").show(200);
}

function hideWaiting()
{
	$("#content").show(200);
	$("#loading").hide(200);
}

function setTitle(str){
	$("#title").text(str);
}

function loadPosts()
{
	clearNav();
	shownavWaiting();
	setTitle("jBloggyAppy AJAX")
	$.get("/jBloggyAppy/Post/json", function(data)
	{
		hidenavWaiting();
		setTitle("Post");
		setHash("posts")
		data = data["Data"];
		$("#nav").append("<ul></ul>");
		for(var i in data)
		{
			var title = data[i]["title"]
			$("#nav ul").append(
				$("<li></li>").append(
					$("<a></a>").attr("href", title).text(title).click(function()
						{
							loadArticle(title);
							return false;
						})
				)
			)
		}
	}, "json");
}

function loadArticle(title)
{
	
	clearContent();
	showWaiting();
	$.get("/jBloggyAppy/Article/"+ title + "/json", function(data)
	{
		hideWaiting();
		$("#content").append("<table></table>");
		setHash("article/" + title);
		var table = $("#content table");
		table.append($("<tr><td>Author</td><td>" + data["author"] + "</td></tr>"))
		table.append($("<tr><td>Body</td><td>" + data["body"] + "</td></tr>"))
		table.append($("<tr><td>Date</td><td>" + data["pubDate"] + "</td></tr>"))
		table.append($("<tr><td>Author</td><td>" + data["author"] + "</td></tr>"))
	}, "json");
}
