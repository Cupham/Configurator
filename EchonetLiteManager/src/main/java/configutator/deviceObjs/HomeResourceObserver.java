package configutator.deviceObjs;



public class HomeResourceObserver extends DataChangeObserver{
	public HomeResourceObserver(EchonetLiteDevice eDevice) {
		eDevice.getProfileObj().attach(this);
		for(eDataObject dataObj :eDevice.getDataObjList()) {
			dataObj.attach(this);
		}
	}

	@Override
	public void dataUpdated(Object obj, String property) {
		if(obj.getClass().equals(eTemperatureSensor.class)) {
			
		}else if(obj.getClass().equals(eLighting.class)) {
			
		} else if(obj.getClass().equals(eElectricConsent.class)) {
		}
		
		
	}

	@Override
	public void dataObjectUpdated() {
		// TODO Auto-generated method stub
		
	}
	

}
