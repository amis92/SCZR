package view;

import mockup.MockupBus;
import mockup.MockupBusStop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateusz on 27.05.14.
 */
public class ViewModel {
    private List<BusStopView> busStopViewList = new ArrayList<>();
    private List<BusView> busViewList = new ArrayList<BusView>();
    private DetailView currentDetailView;
    private final int BUS_WIDTH = 40;
    private final int BUS_HEIGHT = 20;
    private int version;
    private static int prevVersion = 0;
    public ViewModel(){

        currentDetailView = null;
    }

    public void udateBusStopList(List<MockupBusStop> busStopList){
        List<BusStopView> busStopViewListOld = this.busStopViewList;
        this.busStopViewList = new ArrayList<>();
        BusStopView busStopView;
        int i = 0;
        for (MockupBusStop busStop: busStopList) {

            busStopView = findBusStopView(busStopViewListOld,busStop);
            if(busStopView == null){
                busStopView = new BusStopView(busStop);
            }else{
                busStopView.updateView(busStop);
            }

            busStopView.setMiniViewSize(BUS_WIDTH+20, BUS_HEIGHT+10);
            busStopView.setMiniViewPosition(380, i*(BUS_HEIGHT + 12));
            busStopViewList.add(busStopView);
            i++;
            
        }
    }

    private BusStopView findBusStopView(List<BusStopView> busStopViewList, MockupBusStop mockupBusStop){
        for(BusStopView busStopView: busStopViewList){
            if(busStopView.isViewOf(mockupBusStop)){
                return busStopView;
            }
        }
        return null;
    }

    public void updateBusList(List<MockupBus> busList){
        List<BusView> busViewListOld = this.busViewList;
        this.busViewList = new ArrayList<>();
        BusView busView;
        int i = 0;
        for (MockupBus bus: busList) {
            busView = findBusView(busViewListOld, bus);
            if(busView == null){
                busView = new BusView(bus);
            }else{
                busView.updateView(bus);
            }
            busView.setMiniViewSize(BUS_WIDTH, BUS_HEIGHT);
            busView.setMiniViewPosition(10, i * (BUS_HEIGHT + 2));
            busViewList.add(busView);
            i++;
        }
        version = prevVersion++;
    }

    private BusView findBusView(List<BusView> busViewList, MockupBus bus) {
        for(BusView busView : busViewList){
            if(busView.isViewOf(bus)){
                return busView;
            }
        }
        return null;
    }

    public int getVersion(){
      return version;
    }

    public DetailView getCurrentDetailView() {
        return currentDetailView;
    }

    public List<BusView> getBusViewList() {
        return busViewList;
    }

    public void setCurrentDetailView(DetailView currentDetailView) {
        this.currentDetailView = currentDetailView;
    }

    public List<BusStopView> getBusStopViewList() {
        return busStopViewList;
    }
}
