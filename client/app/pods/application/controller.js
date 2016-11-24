import Ember from 'ember';

export default Ember.Controller.extend({
	loginFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	isRegistered: true,

	routes: Ember.computed('model.snapshots.[]', 'model.configuration', function() {
		var routes = [{
			route: 'profile',
			alias: 'Profile',
			icon: 'person'
		}, {
			route: 'analytics',
			alias: 'Analytics',
			icon: 'layers'
		}, {
			route: 'control',
			alias: 'Control',
			icon: 'settings'
		}, {
			route: 'logout',
			alias: 'Sign Out',
			icon: 'exit-to-app'
		}];
		return routes;
	}),

	session: {
		isAuthenticated: false
	},

	actions: {
		transitionTo: function(e) {
			if(e.route == "logout") {
				this.set('session.isAuthenticated', false);
			}
			this.transitionToRoute(e.route);
		},

		login: function() {
			this.setProperties({
				loginFailed: false,
				isProcessing: true
			});

			this.set("timeout", setTimeout(this._actions.slowConnection.bind(this), 5000));

			var host = this.store.adapterFor('application').get('host'),
					namespace = this.store.adapterFor('application').namespace,
					postUrl = [ host, namespace, 'authenticate' ].join('/');
			var request = $.post(postUrl, this.getProperties("username", "password"));
			this.set('loading', true);
			request.then(this._actions.success.bind(this), this._actions.failure.bind(this));
		},

		success: function() {
			var self = this;
			this_actions.reset(self);
			this.set("loginFailed", false);
			this.set('loading', false);
			this.set('session.isAuthenticated', true);
			document.location = "/welcome";
		},

		failure: function() {
			var self = this;
			this._actions.reset(self);
			this.set('loading', false);
			this.set("loginFailed", true);
		},

		slowConnection: function() {
			this.set("isSlowConnection", true);
		},

		reset: function(self) {
			clearTimeout(self.get("timeout"));
			self.setProperties({
				isProcessing: false,
				isSlowConnection: false
			});
		},

		registered: function() {
			this.set("isRegistered", false);
		}
	}
});
