import Ember from 'ember';

export default Ember.Controller.extend({
	loginFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	isRegistered: true,
	isAdmin: false,
	username: "aemarasmy",

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
		if(this.get("isAdmin")) {
			routes = [{
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
				route: 'newuser',
				alias: 'Add New User',
				icon: 'person'
			}, {
				route: 'logout',
				alias: 'Sign Out',
				icon: 'exit-to-app'
			}];
		}
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
			var self = this;
			this.set('loading', true);
			$.ajax({
			    url: postUrl,
			    type: "POST",
			    data: this.getProperties("username", "password"),
					dataType: 'json',
					async: true,
			    success: function (response) {
							self._actions.success(self,response);
    			},
					error: function (jqXHR, textStatus, errorThrown) {
							self._actions.failure(self);
    			}
			});
		},

		success: function(self,response) {
			self._actions.reset(self);
			self.set("loginFailed", false);
			self.set('loading', false);
			self.set('session.isAuthenticated', true);
			self.set('isAdmin',response.data.attributes.isadmin)
			if (self.get('currentRouteName') == "index") {
					self.transitionToRoute("dashboard");
			}
		},

		failure: function(self) {
			self._actions.reset(self);
			self.set('loading', false);
			self.set("loginFailed", true);
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
