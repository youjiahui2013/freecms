package cn.freeteam.cms.action.member;

import java.util.List;
import java.util.Map;

import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Store;
import cn.freeteam.cms.service.StoreService;
import cn.freeteam.util.Pager;

/**
 * 
 * <p>Title: StoreAction.java</p>
 * 
 * <p>Description: 收藏相关操作</p>
 * 
 * <p>Date: Feb 6, 2013</p>
 * 
 * <p>Time: 2:08:47 PM</p>
 * 
 * <p>Copyright: 2013</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class StoreAction extends BaseAction{

	private StoreService storeService;
	private Store store;
	private List<Store> storeList;

	private String order=" storetime desc ";
	private String ids;
	private Map<String, String> objtypes;
	
	public StoreAction() {
		init("storeService");
	}

	/**
	 * 列表
	 * @return
	 */
	public String list(){
		if (store==null ){
			store=new Store();
		}
		objtypes=store.getObjtypes();
		if (order.trim().length()==0) {
			order=" storetime desc ";
		}
		store.setMemberid(getLoginMember().getId());
		storeList=storeService.find(store, order, currPage, pageSize);
		totalCount=storeService.count(store);
		Pager pager=new Pager(getHttpRequest());
		pager.appendParam("store.objtype");
		pager.appendParam("store.objtitle");
		pager.appendParam("store.sitename");
		pager.appendParam("store.channelname");
		pager.appendParam("order");
		pager.appendParam("pageSize");
		pager.appendParam("pageFuncId");
		pager.setCurrPage(currPage);
		pager.setPageSize(pageSize);
		pager.setTotalCount(totalCount);
		pager.setOutStrNoTable("store_list.do");
		pageStr=pager.getOutStrNoTable();
		return "list";
	}
	/**
	 * 删除
	 * @return
	 */
	public String del(){
		if (ids!=null && ids.trim().length()>0) {
			StringBuilder sb=new StringBuilder();
			String[] idArr=ids.split(";");
			if (idArr!=null && idArr.length>0) {
				for (int i = 0; i < idArr.length; i++) {
					if (idArr[i].trim().length()>0) {
						try {
							storeService.del(idArr[i]);
							sb.append(idArr[i]+";");
						} catch (Exception e) {
							DBProException(e);
						}
					}
				}
			}
			write(sb.toString(), "UTF-8");
		}
		return null;
	}
	public StoreService getStoreService() {
		return storeService;
	}
	public void setStoreService(StoreService storeService) {
		this.storeService = storeService;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public List<Store> getStoreList() {
		return storeList;
	}
	public void setStoreList(List<Store> storeList) {
		this.storeList = storeList;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}

	public Map<String, String> getObjtypes() {
		return objtypes;
	}

	public void setObjtypes(Map<String, String> objtypes) {
		this.objtypes = objtypes;
	}
}
