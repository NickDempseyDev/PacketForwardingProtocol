forwarding_protocol = Proto("forwarding_protocol",  "ForwardingProtocol")

packet_type = ProtoField.uint8("forwarding_protocol.packet_type", "PacketType", base.DEC)
type_lv = ProtoField.uint8("forwarding_protocol.type_lv", "Type", base.DEC)
t_length_v = ProtoField.uint8("forwarding_protocol.t_length_v", "Length", base.DEC)
tl_value = ProtoField.string("forwarding_protocol.tl_value"     , "Value"    , base.ASCII)
payload_length = ProtoField.uint8("forwarding_protocol.payload_length", "Payload Length", base.DEC)
payload_content = ProtoField.string("forwarding_protocol.payload_content"     , "Payload"    , base.ASCII)
next_hop = ProtoField.string("forwarding_protocol.next_hop"     , "Next Hop"    , base.ASCII)
next_hop_l = ProtoField.uint8("forwarding_protocol.next_hop_t", "Next Hop Length", base.DEC)
from_ip = ProtoField.string("forwarding_protocol.from_ip"     , "From IP"    , base.ASCII)

forwarding_protocol.fields = { packet_type, type_lv, t_length_v, tl_value, payload_length, payload_content, next_hop, from_ip }


function get_packet_name(type)
  local type_name = "Unknown"
      if type ==    1 then type_name = "ROUTER"
  elseif type ==    2 then type_name = "ENDPOINT"
  elseif type ==    3 then type_name = "ACK"
  elseif type ==    4 then type_name = "CONTROLLER REQUEST" 
  elseif type ==    5 then type_name = "CONTROLLER RESPONSE"
  elseif type ==    6 then type_name = "HELLO"
  elseif type ==    7 then type_name = "GOOD TO GO" end
  return type_name
end

function get_type(type)
  local type_status = "Unknown"
      if type ==    1 then type_status = "NET ID"
  elseif type ==    2 then type_status = "COMBINATION OF NET ID"
  elseif type ==    3 then type_status = "NEXT HOP"
  elseif type ==    4 then type_status = "PAYLOAD" 
  elseif type ==    5 then type_status = "FROM IP STRING"
  elseif type ==    6 then type_status = "ROUTER/ENDPOINT IP ADDRESSES"
  elseif type ==    7 then type_status = "END OF PACKET" end
  return type_status
end

function forwarding_protocol.dissector(buffer, pinfo, tree)
  length = buffer:len()
  if length == 0 then return end
  pinfo.cols.protocol = forwarding_protocol.name
  local subtree = tree:add(forwarding_protocol, buffer(), "My Protocol Data")
  -- Get the packet type
  local type = buffer(0,1):le_uint()
  local type_name = get_packet_name(type)
  subtree:add_le(packet_type, buffer(0,1)):append_text(" (" .. type_name .. ")")
  -- If its an ack just return
  if type == 3 then
    return 
  end

  -- start TLV
  -- local type_one = buffer(1,1):le_uint()
  -- local type_one_str = get_type(type_one)
  -- subtree:add_le(type_lv, type_one):append_text(" (" .. type_one_str .. ")")

  -- Start going through TLV
  local curr_pack_pos = 1
  while( buffer(curr_pack_pos,1):le_uint() ~= 7 and curr_pack_pos < buffer:len() - 1 )  -- while not the end of the packet
  do
    -- isolate type
    local subtree_local = subtree:add(forwarding_protocol, buffer(), "TLV")
    local type_ = buffer(curr_pack_pos,1):le_uint()
    local type_str = get_type(type_)
    subtree_local:add_le(type_lv, type_):append_text(" (" .. type_str .. ")")
    curr_pack_pos = curr_pack_pos + 1

    if type_ == 2 then -- if its a combination of net ids
      local number_of_netids = buffer(curr_pack_pos, 1):le_uint()
      subtree_local:add_le(t_length_v, number_of_netids):append_text(" (" .. "number of net ids" .. ")")
      curr_pack_pos = curr_pack_pos + 1
      
      local net_str = ""
      while( number_of_netids > 0 )
      do
        local len = buffer(curr_pack_pos, 1):le_uint()
        curr_pack_pos = curr_pack_pos + 1
        local str = buffer(curr_pack_pos, len)
        curr_pack_pos = curr_pack_pos + len

        if number_of_lvs == 1 then
          net_str = net_str .. str
          number_of_netids = number_of_netids - 1
        else
          net_str = net_str .. str .. "."
          number_of_netids = number_of_netids - 1
        end

      end
      subtree_local:add_le(tl_value, net_str)

    else -- its just a regular tlv

      local lenT = buffer(curr_pack_pos,1):le_uint()
      curr_pack_pos = curr_pack_pos + 1
      subtree_local:add_le(t_length_v, lenT)
      if type_ == 6 then -- if its an ip
        local ip_str = ""
        while( lenT > 0 )
        do
          local curr_byte = buffer(curr_pack_pos, 1):le_uint()
          if lenT == 1 then
            ip_str = ip_str .. curr_byte
          else
            ip_str = ip_str .. curr_byte .. "."
          end
          curr_pack_pos = curr_pack_pos + 1
          lenT = lenT - 1
        end
        subtree_local:add_le(tl_value, ip_str)
      else
        local val = buffer(curr_pack_pos, lenT)
        subtree_local:add_le(tl_value, val)
        curr_pack_pos = curr_pack_pos + lenT
      end
    end
  end
end

function all_ports(buffer, pinfo, tree)

  forwarding_protocol.dissector(buffer, pinfo, tree)
  return true
end

forwarding_protocol:register_heuristic("udp", all_ports)