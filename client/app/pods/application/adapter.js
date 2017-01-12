import DS from 'ember-data';

export default DS.JSONAPIAdapter.extend({
	namespace: 'release-0.0.1-SNAPSHOT/rest/api/smarthome',
  host: 'http://localhost:8080',
	shouldReloadAll: function() {
		return true
	},
	headers: {
		"Accept": "text/javascript, text/html, application/xml, text/xml, */*"
	}
});
