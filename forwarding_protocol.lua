forwarding_protocol = Proto("forwarding_protocol",  "ForwardingProtocol")

packet_type = ProtoField.uint8("forwarding_protocol.packet_type", "PacketType", base.DEC)
type_lv = ProtoField.uint8("forwarding_protocol.type_lv", "Type", base.DEC)
t_length_v = ProtoField.uint8("forwarding_protocol.t_length_v", "Length", base.DEC)
tl_value = ProtoField.string("forwarding_protocol.tl_value"     , "Value"    , base.ASCII)
payload_length = ProtoField.uint8("forwarding_protocol.payload_length", "Payload Length", base.DEC)
payload_content = ProtoField.string("forwarding_protocol.payload_content"     , "Payload"    , base.ASCII)

forwarding_protocol.fields = { packet_type, type_lv, t_length_v, tl_value, payload_length, payload_content }


function get_packet_name(type)
  local type_name = "Unknown"
      if type ==    1 then type_name = "ROUTER"
  elseif type ==    2 then type_name = "ENDPOINT"
  elseif type ==    3 then type_name = "ACK"
  elseif type ==    4 then type_name = "CONTROLLER" end
  return type_name
end

function get_type(type)
  local type_status = "Unknown"
      if type ==    1 then type_status = "NET ID"
  elseif type ==    2 then type_status = "COMBINATION"
  elseif type ==    3 then type_status = "NEXT HOP"
  elseif type ==    4 then type_status = "PAYLOAD" end
  return type_status
end

function forwarding_protocol.dissector(buffer, pinfo, tree)
  length = buffer:len()
  if length == 0 then return end

  pinfo.cols.protocol = forwarding_protocol.name

  local subtree = tree:add(forwarding_protocol, buffer(), "My Protocol Data")

  local type = buffer(0,1):le_uint()
  local type_name = get_packet_name(type)

  subtree:add_le(packet_type, buffer(0,1)):append_text(" (" .. type_name .. ")")

  if type == 3 then
    return 
  end

  -- start TLV
  local type_one = buffer(1,1):le_uint()
  local type_one_str = get_type(type_one)
  subtree:add_le(type_lv, type_one):append_text(" (" .. type_one_str .. ")")
  
  local number_of_lvs = 0
  local current_pos = 0
  if type_one == 1 then
    number_of_lvs = 1
    current_pos = 2
  else
    number_of_lvs = buffer(2,1):le_uint()
    current_pos = 3
  end
  subtree:add_le(t_length_v, number_of_lvs)
  local current_substree_number = 1
  while( number_of_lvs > 0 )
  do
    local subtree_local = subtree:add(forwarding_protocol, buffer(), "TLV"..current_substree_number)
    local length_local = buffer(current_pos,1):le_uint()
    subtree_local:add_le(t_length_v,length_local)
    current_pos = current_pos + 1
    subtree_local:add_le(tl_value, buffer(current_pos,length_local))
    current_pos = current_pos + length_local
    number_of_lvs = number_of_lvs - 1
    current_substree_number = current_substree_number + 1
  end
  local payload_len = buffer(current_pos,1):le_uint()
  subtree:add_le(payload_length,payload_len)
  current_pos = current_pos + 1
  subtree:add_le(payload_content, buffer(current_pos, payload_len))
end

function all_ports(buffer, pinfo, tree)

  forwarding_protocol.dissector(buffer, pinfo, tree)
  return true
end

forwarding_protocol:register_heuristic("udp", all_ports)