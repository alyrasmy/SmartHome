import { moduleFor, test } from 'ember-qunit';

moduleFor('route:dashboard.index.fake', 'Unit | Route | dashboard.index.fake', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
});

test('it exists', function(assert) {
  var route = this.subject();
  assert.ok(route);
});
