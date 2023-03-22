<%@ include file="/libs/granite/ui/global.jsp" %>
<%@ page session="false"
         import="java.util.Iterator,
                 com.adobe.granite.ui.components.AttrBuilder,
                 com.adobe.granite.ui.components.Tag,
                 com.adobe.granite.ui.components.Config,
                 org.apache.sling.api.resource.ValueMap,
                 java.util.stream.StreamSupport,
                 java.util.stream.Collectors,
                 java.util.*"
%>
<%@ page import="org.apache.sling.api.resource.Resource" %>
<%@ page import="java.util.function.Function" %>

<%
  if (!cmp.getRenderCondition(resource, false).check()) {
    return;
  }
  Tag tag = cmp.consumeTag();
  AttrBuilder attrs = tag.getAttrs();
  attrs.addClass("coral-Well");
  cmp.populateCommonAttrs(attrs);
  Config cfg = cmp.getConfig();
  String title = cfg.get("title", String.class);
  String name = cfg.get("name", String.class);
  String cleanName = name != null ? name.replace(".", "").replace("/", "") : "";
  String tableId = "order-table-" + cleanName;
  String[] values = cmp.getValue().getContentValue(name, new String[0]);
  List<String> valuesList = Arrays.asList(values);
  Iterator<Resource> itemsIterator = cmp.getItemDataSource().iterator();
  List<ValueMap> items = StreamSupport.stream(Spliterators.spliteratorUnknownSize(itemsIterator, Spliterator.ORDERED), false)
          .sorted(
                  Comparator.comparing(new Function<Resource, Integer>() {
                    @Override
                    public Integer apply(Resource item) {
                      ValueMap vm = item.getValueMap();
                      String val = vm.get("value", "");
                      return valuesList.indexOf(val);
                    }
                  })
          ).map(Resource::getValueMap)
          .collect(Collectors.toList());
%>

<div <%= attrs.build() %>>
  <table is="coral-table" orderable id="<%=tableId%>">
    <colgroup>
      <col is="coral-table-column">
      <col is="coral-table-column" fixedwidth>
    </colgroup>
    <thead is="coral-table-head">
    <tr is="coral-table-row">
      <th is="coral-table-headercell"><%=title%></th>
      <th is="coral-table-headercell"></th>
    </tr>
    </thead>
    <tbody is="coral-table-body">
    <%
      for (ValueMap vm : items) {
    %>
    <tr is="coral-table-row">
      <td is="coral-table-cell">
        <%=vm.get("text", "") %>
        <input type="hidden" name="<%=name%>" value="<%=vm.get("value", "")%>"/>
      </td>
      <td is="coral-table-cell">
        <button is="coral-button" type="button" variant="minimal" icon="dragHandle" coral-table-roworder></button>
      </td>
    </tr>
    <%
      }
    %>
    </tbody>
  </table>
</div>