<html>
	<head>
		<title>/${path}</title>
	</head>
	<body>
		<div>/${path}</div>
		<#if contentNode.parent??>
			<div style="padding-top: 20px;"><a href="/jc-rest/services/template/ftl/browser/nodes${contentNode.parent}.html?includeAllProperties=true&includeAllChildren=true">..</a></div>
		</#if>
		<div style="padding-top: 20px;">
			<div style="padding-bottom: 10px;text-decoration: underline;">Properties</div>
			<#list contentNode.properties as prop>
				<div>
					<div style="float: left;width: 25%;">${prop.name}</div>
					<div style="float: right;width: 75%;">
						<#list prop.values as value>
							${value?html}<#if value_has_next>,</#if>
						</#list>
					</div>
				</div>
				<div style="clear: both;"></div>
			</#list>
		</div>
		<div style="padding-top: 20px;">
			<div style="padding-bottom: 10px;text-decoration: underline;">Nodes</div>
			<#list contentNode.nodes as node>
	    		<div><a href="/jc-rest/services/template/ftl/browser/nodes${node.parent}/${node.name}.html?includeAllProperties=true&includeAllChildren=true">${node.name}</a></div>
	    	</#list>
	    </div>
	</body>
</html>